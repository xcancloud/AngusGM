package cloud.xcan.angus.core.gm.application.cmd.sms.impl;

import static cloud.xcan.angus.api.commonlink.SmsConstants.VC_PARAM_NAME;
import static cloud.xcan.angus.api.commonlink.SmsConstants.VC_TEMPLATE_VALID_MINUTE;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotEmpty;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.converter.SmsConverter.getVerificationCodeCacheKey;
import static cloud.xcan.angus.core.gm.application.converter.SmsConverter.getVerificationCodeRepeatCheckKey;
import static cloud.xcan.angus.core.gm.domain.SmsMessage.SMS_RECEIVER_IS_MISSING;
import static cloud.xcan.angus.core.gm.domain.SmsMessage.SMS_SEND_ERROR_CODE;
import static cloud.xcan.angus.core.gm.domain.SmsMessage.VERIFY_CODE_ERROR;
import static cloud.xcan.angus.core.gm.domain.SmsMessage.VERIFY_CODE_EXPIRED;
import static cloud.xcan.angus.core.gm.domain.SmsMessage.VERIFY_CODE_MOBILES_IS_MISSING;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isUserAction;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.commonlink.to.TORoleUserRepo;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUserRepo;
import cloud.xcan.angus.api.commonlink.user.group.GroupUserRepo;
import cloud.xcan.angus.api.enums.ProcessStatus;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.biz.exception.BizException;
import cloud.xcan.angus.core.biz.exception.NoRollbackException;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.query.sms.SmsChannelQuery;
import cloud.xcan.angus.core.gm.application.query.sms.SmsQuery;
import cloud.xcan.angus.core.gm.domain.sms.InputParam;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.domain.sms.SmsRepo;
import cloud.xcan.angus.core.gm.domain.sms.channel.SmsChannel;
import cloud.xcan.angus.core.gm.domain.sms.template.SmsTemplate;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.GsonUtils;
import cloud.xcan.angus.extension.sms.api.MessageChannel;
import cloud.xcan.angus.extension.sms.api.SmsProvider;
import cloud.xcan.angus.lettucex.util.RedisService;
import cloud.xcan.angus.remote.message.AbstractResultMessageException;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import cloud.xcan.angus.spec.experimental.SimpleResult;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of SMS command operations.
 * </p>
 * <p>
 * Manages SMS sending, verification code handling, and recipient management.
 * Supports various recipient types including users, departments, groups, and policies.
 * </p>
 * <p>
 * Provides immediate sending for verification codes and test messages,
 * with job-based scheduling for bulk messages.
 * </p>
 */
@Biz
@Slf4j
@DependsOn({"pluginManager"})
public class SmsCmdImpl extends CommCmd<Sms, Long> implements SmsCmd {

  @Resource
  private SmsRepo smsRepo;
  @Resource
  private SmsQuery smsQuery;
  @Resource
  private SmsChannelQuery smsChannelQuery;
  @Resource
  private UserRepo userRepo;
  @Resource
  private GroupUserRepo groupUserRepo;
  @Resource
  private DeptUserRepo deptUserRepo;
  @Resource
  private TORoleUserRepo toRoleUserRepo;
  @Resource
  private ObjectMapper objectMapper;
  @Resource
  private RedisService<String> stringRedisService;

  /**
   * <p>
   * Sends SMS messages with comprehensive validation and processing.
   * </p>
   * <p>
   * Validates recipient information, channel availability, and template configuration.
   * Supports both mobile-based and organization-based sending with pagination for large recipient lists.
   * </p>
   * <p>
   * Verification code SMS and channel test SMS are sent immediately.
   * Note: Future enhancement needed to support resending after failure.
   * </p>
   */
  @DoInFuture("Support resending after failure")
  @Override
  public void send(Sms sms, boolean testChannel) {
    new BizTemplate<Void>() {
      SmsChannel enabledChannel;
      SmsTemplate smsTemplate;
      SmsProvider smsProvider;

      @Override
      protected void checkParams() {
        // Verify sender: One of verification parameters toAddress and objectIds is required
        assertTrue(sms.isSendByMobiles() || sms.isSendByOrgType(), SMS_RECEIVER_IS_MISSING);

        // Verify mobiles are required when sending verification code
        assertTrue(!sms.getVerificationCode() || isNotEmpty(sms.getInputParamData().getMobiles()),
            VERIFY_CODE_MOBILES_IS_MISSING);

        // Verify mobile format
        smsQuery.checkMobileFormat(sms);

        // Verify send verification code repeatedly
        if (sms.getVerificationCode()) {
          smsQuery.checkVerifyCodeSendRepeated(sms);
        }

        // Verify enabled channel exists
        if (testChannel) {
          enabledChannel = smsChannelQuery.detail(sms.getChannelId());
        } else {
          enabledChannel = smsQuery.checkChannelEnabledAndGet();
        }

        // Verify SMS template exists
        smsTemplate = smsQuery.checkTemplateAndGet(sms, enabledChannel);

        // Verify and get enabled channel plugin SmsProvider
        if (sms.isSendNow()) {
          smsProvider = smsQuery.checkAndGetSmsProvider(enabledChannel);
        }
      }

      @Override
      protected Void process() {
        // Don't support SMS in other regions, need to apply for a foreign channel and it is expensive to send SMS
        sms.setLanguage(SupportedLanguage.zh_CN);

        // Sending by mobiles has higher priority than sending by orgType
        if (sms.isSendByMobiles()) {
          send0(testChannel, sms, smsTemplate, enabledChannel, smsProvider);
        } else {
          // When both types exist, sending by orgType will be ignored
          int page = 0, size = 500;
          List<String> pageMobiles = getReceiveObjectMobiles(sms.getReceiveObjectType(),
              sms.getReceiveObjectIds(), sms.getReceivePolicyCodes(), page, size);
          if (isEmpty(pageMobiles)) {
            log.warn("The receiver's mobile is not found, SMS: {}", GsonUtils.toJson(sms));
            return null;
          }
          do {
            // If sending by orgType, it will be transferred to sending by mobiles
            sms.getInputParamData().setMobiles(new HashSet<>(pageMobiles));
            send0(testChannel, sms, smsTemplate, enabledChannel, smsProvider);
            if (!sms.getReceiveObjectType().equals(ReceiveObjectType.USER)) {
              pageMobiles = getReceiveObjectMobiles(sms.getReceiveObjectType(),
                  sms.getReceiveObjectIds(), sms.getReceivePolicyCodes(), ++page, size);
              if (isNotEmpty(pageMobiles)) {
                // If sending by type requires multiple times, generate a new split SMS ID
                sms.setId(uidGenerator.getUID());
              }
            } else {
              // Query only once, max to 500
              pageMobiles = null;
            }
          } while (nonNull(pageMobiles) && !pageMobiles.isEmpty());
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Sends SMS messages by job processing.
   * </p>
   * <p>
   * Used for scheduled SMS sending with pre-validated channel and provider.
   * Verification code SMS and channel test SMS are sent immediately.
   * Note: Future enhancement needed to support resending after failure.
   * </p>
   */
  @DoInFuture("Support resending after failure")
  @Override
  public void sendByJob(Sms sms, SmsChannel enabledChannel, SmsProvider smsProvider) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        SmsTemplate smsTemplate = smsQuery.checkTemplateAndGet(sms, enabledChannel);
        send0(false, sms, smsTemplate, enabledChannel, smsProvider);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Verifies SMS verification code.
   * </p>
   * <p>
   * Validates verification code against cached value and removes cache after successful verification.
   * </p>
   */
  @Override
  public void checkVerificationCode(SmsBizKey bizKey, String mobile, String verificationCode) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        String cacheKey = getVerificationCodeCacheKey(bizKey, mobile);
        String cachedVc = stringRedisService.get(cacheKey);
        assertNotEmpty(cachedVc, VERIFY_CODE_EXPIRED);
        assertTrue(equalsIgnoreCase(verificationCode, cachedVc), VERIFY_CODE_ERROR);
        deleteVerificationCodeCache(cacheKey, bizKey, mobile);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Deletes SMS records.
   * </p>
   * <p>
   * Removes SMS records from the database.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(HashSet<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        smsRepo.deleteByIdIn(ids);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Updates SMS records in batch.
   * </p>
   * <p>
   * Updates multiple SMS records in a single operation.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update0(List<Sms> sms) {
    batchUpdate0(sms);
  }

  /**
   * <p>
   * Sends SMS with comprehensive processing and error handling.
   * </p>
   * <p>
   * Handles both immediate sending and job-based scheduling with proper error handling
   * and verification code caching.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class, noRollbackFor = NoRollbackException.class)
  public void send0(boolean testChannel, Sms sms, SmsTemplate smsTemplate,
      SmsChannel enabledChannel, SmsProvider smsProvider) {
    assembleSmsSendParam(testChannel, sms, smsTemplate, enabledChannel);

    sms.setId(Objects.isNull(sms.getId()) ? uidGenerator.getUID() : sms.getId());
    sms.setTemplateCode(smsTemplate.getCode());
    // Retry is unsupported
    sms.setSendRetryNum(0);

    // ** Send now **
    if (testChannel || sms.getVerificationCode() || sms.isSendNow()) {
      try {
        // Send urgent SMS directly
        SimpleResult simpleResult = sendSmsNow(sms, smsProvider, smsTemplate, enabledChannel);
        setSendSmsResultStatus(simpleResult, sms);
        smsRepo.save(sms);

        // Cache verification code when it is sent successfully
        if (sms.getVerificationCode()) {
          cacheVerificationCode(sms);
        }
        return;
      } catch (Exception e) {
        log.error("Sending SMS exception, SMS: {}, exception: {}", GsonUtils.toJson(sms),
            e.getMessage());

        sms.setSendStatus(ProcessStatus.FAILURE).setFailureReason(e.getMessage());
        // insert0(sms); // Fix:: When sending again, it should be updated rather than inserted
        smsRepo.save(sms);

        // Do not roll back when sending exceptions
        translateSendNoRollbackException(e);
        return;
      }
    }

    // ** Send in future by job **
    // Save and wait for timing sending of job
    sms.setSendStatus(ProcessStatus.PENDING);
    // Makes it possible to query it when the job is sent
    if (isNull(sms.getExpectedSendDate())) {
      sms.setExpectedSendDate(LocalDateTime.now());
    }
    smsRepo.save(sms);
  }

  /**
   * <p>
   * Sends SMS immediately using the SMS provider.
   * </p>
   * <p>
   * Assembles third-party parameters and sends SMS through the provider.
   * </p>
   */
  @SneakyThrows
  private SimpleResult sendSmsNow(Sms sms, SmsProvider smsProvider, SmsTemplate smsTemplate,
      SmsChannel enabledChannel) {
    // Assemble and use the plugin to send SMS requests
    cloud.xcan.angus.extension.sms.api.Sms pluginSms = assembleThirdParams(sms, smsTemplate);
    sms.setThirdInputParam(objectMapper.writeValueAsString(pluginSms));
    return smsProvider.sendSms(pluginSms, getMessageChannel(enabledChannel));
  }

  /**
   * <p>
   * Gets mobile numbers for different recipient object types.
   * </p>
   * <p>
   * Supports various recipient types including tenant, user, department, group, policy, and all users.
   * </p>
   * <p>
   * @param receiveObjectType  {@link ReceiveObjectType}
   * @param receiveObjectIds   receive object ids
   * @param receivePolicyCodes receive policy codes
   * @param page               zero-based page index, must not be negative.
   * @param size               the size of the page to be returned, must be greater than 0.
   * @return mobiles
   * </p>
   */
  public List<String> getReceiveObjectMobiles(ReceiveObjectType receiveObjectType,
      List<Long> receiveObjectIds, List<String> receivePolicyCodes, int page, int size) {
    List<String> pageMobiles = null;
    switch (receiveObjectType) {
      case TENANT:
        pageMobiles = userRepo.findValidMobileByTenantIdIn(receiveObjectIds,
            PageRequest.of(page, size, Sort.Direction.DESC, "id")).getContent();
        break;
      case USER:
        pageMobiles = userRepo.findValidMobileByIdIn(receiveObjectIds);
        break;
      case DEPT:
        pageMobiles = deptUserRepo.findValidMobileByDeptIds(receiveObjectIds, page * size, size);
        break;
      case GROUP:
        pageMobiles = groupUserRepo.findValidMobileByGroupIds(receiveObjectIds, page * size, size);
        break;
      case POLICY:
        // TODO: Implement policy-based mobile retrieval
        break;
      case TO_POLICY:
        pageMobiles = toRoleUserRepo
            .findValidMobileByRoleCodes(receivePolicyCodes, page * size, size);
        break;
      case ALL:
        pageMobiles = userRepo
            .findValidAllMobile(PageRequest.of(page, size, Sort.Direction.DESC, "id")).getContent();
        break;
      default:
        // NOOP for unsupported recipient types
    }
    return Objects.nonNull(pageMobiles) ? pageMobiles.stream()
        .filter(ObjectUtils::isNotEmpty).collect(Collectors.toList()) : null;
  }

  /**
   * <p>
   * Sets SMS sending result status.
   * </p>
   * <p>
   * Updates SMS status based on sending result and handles user action vs job action differently.
   * </p>
   */
  @SneakyThrows
  private void setSendSmsResultStatus(SimpleResult simpleResult, Sms sms) {
    if (simpleResult.isSuccess()) {
      sms.setSendStatus(ProcessStatus.SUCCESS);
      sms.setActualSendDate(LocalDateTime.now());
      sms.setThirdOutputParam(objectMapper.writeValueAsString(simpleResult));
    } else {
      // Does not meet the conditions for sending SMS or network problem
      if (isUserAction()) {
        // If user action throw exception directly
        throw BizException.of(SMS_SEND_ERROR_CODE, simpleResult.getMessage());
      } else {
        // If job mark a failure
        log.error("Send sms failure, cause: {}", simpleResult.getMessage());
        sms.setThirdOutputParam(objectMapper.writeValueAsString(simpleResult));
        sms.setSendStatus(ProcessStatus.FAILURE);
      }
    }
  }

  /**
   * <p>
   * Caches verification code for validation.
   * </p>
   * <p>
   * Stores verification code in Redis with expiration time and repeat check key.
   * </p>
   */
  private void cacheVerificationCode(Sms sms) {
    final InputParam inputParamData = sms.getInputParamData();
    for (String mobile : inputParamData.getMobiles()) {
      String verificationCode = String
          .valueOf(inputParamData.getTemplateParams().get(VC_PARAM_NAME));
      int verificationCodeValidMinute = Integer.parseInt(inputParamData.
          getTemplateParams().get(VC_TEMPLATE_VALID_MINUTE));
      stringRedisService.set(getVerificationCodeCacheKey(inputParamData.getBizKey(), mobile),
          verificationCode, verificationCodeValidMinute, TimeUnit.MINUTES);
      stringRedisService.set(getVerificationCodeRepeatCheckKey(inputParamData.getBizKey(),
          mobile), verificationCode, 1, TimeUnit.MINUTES);
    }
  }

  /**
   * <p>
   * Deletes verification code cache after successful verification.
   * </p>
   * <p>
   * Removes both verification code and repeat check cache entries.
   * </p>
   */
  private void deleteVerificationCodeCache(String cacheKey, SmsBizKey bizKey, String mobile) {
    stringRedisService.delete(cacheKey);
    stringRedisService.delete(getVerificationCodeRepeatCheckKey(bizKey, mobile));
  }

  /**
   * <p>
   * Assembles SMS sending parameters.
   * </p>
   * <p>
   * Prepares SMS parameters for test channels and verification codes.
   * </p>
   */
  private void assembleSmsSendParam(boolean testChannel, Sms sms, SmsTemplate smsTemplate,
      SmsChannel enabledSmsChannel) {
    if (testChannel) {
      assembleTestChannelParams(sms, enabledSmsChannel);
    }

    if (sms.getVerificationCode()) {
      assembleVerificationCodeParams(sms, smsTemplate);
    }
  }

  /**
   * <p>
   * Assembles and sends SMS verification code parameters.
   * </p>
   * <p>
   * Generates verification code if not provided and sets expiration time.
   * </p>
   */
  private void assembleVerificationCodeParams(Sms sms, SmsTemplate template) {
    InputParam inputParamData = sms.getInputParamData();
    if (isNotEmpty(inputParamData.getTemplateParams())) {
      Map<String, String> paramMap = new HashMap<>(inputParamData.getTemplateParams());
      paramMap.compute(VC_PARAM_NAME,
          (k, inputVc) -> isEmpty(inputVc) ? randomNumeric(6) : inputVc);
      paramMap.put(VC_TEMPLATE_VALID_MINUTE,
          String.valueOf(getVerificationCodeExpireInMinute(inputParamData, template)));
      inputParamData.setTemplateParams(paramMap);
    } else {
      Map<String, String> paramMap = new HashMap<>();
      paramMap.put(VC_PARAM_NAME, randomNumeric(6));
      paramMap.put(VC_TEMPLATE_VALID_MINUTE, String.valueOf(getVerificationCodeExpireInMinute(
          inputParamData, template)));
      inputParamData.setTemplateParams(paramMap);
    }
  }

  /**
   * <p>
   * Assembles test channel parameters.
   * </p>
   * <p>
   * Sets channel type parameter for test messages.
   * </p>
   */
  private void assembleTestChannelParams(Sms sms, SmsChannel enabledChannel) {
    Map<String, String> templateParams = new HashMap<>();
    templateParams.put("channelType", enabledChannel.getName());
    sms.getInputParamData().setTemplateParams(templateParams);
  }

  /**
   * <p>
   * Assembles third-party request parameters for sending SMS.
   * </p>
   * <p>
   * Creates SMS object with mobile numbers, signature, template code, and parameters.
   * </p>
   */
  private cloud.xcan.angus.extension.sms.api.Sms assembleThirdParams(Sms sms,
      SmsTemplate smsTemplate) {
    return new cloud.xcan.angus.extension.sms.api.Sms()
        .setMobiles(Lists.newArrayList(sms.getInputParamData().getMobiles()))
        .setSign(smsTemplate.getSignature())
        .setTemplateCode(smsTemplate.getThirdCode())
        .setTemplateParams(sms.getInputParamData().getTemplateParams());
  }

  /**
   * <p>
   * Gets verification code expiration time in minutes.
   * </p>
   * <p>
   * Uses provided expiration time or template default, converted to minutes.
   * </p>
   */
  private Integer getVerificationCodeExpireInMinute(InputParam inputParamData,
      SmsTemplate smsTemplate) {
    Integer expire = nonNull(inputParamData.getExpire()) && inputParamData.getExpire() > 0
        ? inputParamData.getExpire() : smsTemplate.getVerificationCodeValidSecond();
    return expire / 60;
  }

  /**
   * <p>
   * Creates message channel from SMS channel configuration.
   * </p>
   * <p>
   * Maps SMS channel settings to message channel object for provider communication.
   * </p>
   */
  private MessageChannel getMessageChannel(SmsChannel smsChannel) {
    return new MessageChannel().setName(smsChannel.getName())
        .setLogo(smsChannel.getLogo())
        .setEndpoint(smsChannel.getEndpoint())
        .setAccessKeyId(smsChannel.getAccessKeyId())
        .setAccessKeySecret(smsChannel.getAccessKeySecret())
        .setThirdChannelNo(smsChannel.getThirdChannelNo());
  }

  /**
   * <p>
   * Translates sending exceptions to no-rollback exceptions.
   * </p>
   * <p>
   * Converts sending failures to appropriate exception types that don't trigger transaction rollback.
   * </p>
   */
  private void translateSendNoRollbackException(Exception e) {
    if (e instanceof AbstractResultMessageException) {
      throw NoRollbackException.of(((AbstractResultMessageException) e).getCode(), e.getMessage());
    } else {
      throw NoRollbackException.of(SMS_SEND_ERROR_CODE, e.getMessage());
    }
  }

  @Override
  protected BaseRepository<Sms, Long> getRepository() {
    return smsRepo;
  }
}
