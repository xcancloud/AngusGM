package cloud.xcan.angus.core.gm.application.cmd.email.impl;

import static cloud.xcan.angus.api.commonlink.EmailConstant.MAIL_CHANNEL_VARIABLE_PREFIX;
import static cloud.xcan.angus.api.commonlink.EmailConstant.VC_PARAM_NAME;
import static cloud.xcan.angus.api.commonlink.EmailConstant.VC_REPEAT_SEND_LIMIT_SECOND;
import static cloud.xcan.angus.api.commonlink.EmailConstant.VC_VARIABLE;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotEmpty;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.converter.EmailConverter.getVerificationCodeCacheKey;
import static cloud.xcan.angus.core.gm.application.converter.EmailConverter.getVerificationCodeRepeatCheckKey;
import static cloud.xcan.angus.core.gm.application.converter.EmailConverter.getVerificationCodeValidSecond;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.EMAIL_RECEIVER_IS_MISSING;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.SEND_EXCEPTION_CODE;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.TEMPLATE_BIZ_KEY_NOT_NULL;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.TEMPLATE_IO_EXCEPTION;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.TEMPLATE_IO_EXCEPTION_CODE;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.TEMPLATE_PARSE_EXCEPTION;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.TEMPLATE_PARSE_EXCEPTION_CODE;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.VERIFY_CODE_ERROR;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.VERIFY_CODE_EXPIRED;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.VERIFY_CODE_TO_ADDRESS_IS_MISSING;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isJob;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isUserAction;
import static cloud.xcan.angus.spec.utils.JsonUtils.toJson;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.to.TORoleUserRepo;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUserRepo;
import cloud.xcan.angus.api.commonlink.user.group.GroupUserRepo;
import cloud.xcan.angus.api.enums.ProcessStatus;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.biz.exception.NoRollbackException;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.query.email.EmailQuery;
import cloud.xcan.angus.core.gm.application.query.email.EmailServerQuery;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailRepo;
import cloud.xcan.angus.core.gm.domain.email.server.EmailProtocol;
import cloud.xcan.angus.core.gm.domain.email.server.EmailServer;
import cloud.xcan.angus.core.gm.domain.email.template.EmailTemplate;
import cloud.xcan.angus.core.gm.infra.mail.EmailSender;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.GsonUtils;
import cloud.xcan.angus.lettucex.util.RedisService;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import freemarker.template.TemplateException;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.MailSendException;
import org.springframework.transaction.annotation.Transactional;


@Biz
@Slf4j
public class EmailCmdImpl extends CommCmd<Email, Long> implements EmailCmd {

  @Resource
  private EmailRepo emailRepo;

  @Resource
  private EmailQuery emailQuery;

  @Resource
  private EmailServerQuery emailServerQuery;

  @Resource
  private UserRepo userRepo;

  @Resource
  private GroupUserRepo groupUserRepo;

  @Resource
  private DeptUserRepo deptUserRepo;

  @Resource
  private TORoleUserRepo toRoleUserRepo;

  @Resource
  private EmailSender emailSender;

  @Resource
  private RedisService<String> stringRedisService;

  @Override
  public void send(Email email, boolean testServer) {
    new BizTemplate<Void>() {
      EmailTemplate emailTemplateDb = null;
      EmailServer emailServerDb = null;

      @Override
      protected void checkParams() {
        // Check the sender:One of the verification parameters toAddress and objectIds is required
        assertTrue(email.isSendByToAddress() || email.isSendByOrgType(),
            EMAIL_RECEIVER_IS_MISSING);

        // Check addresses is required when sending the verification code
        assertTrue(!email.getVerificationCode()
            || isNotEmpty(email.getToAddrData()), VERIFY_CODE_TO_ADDRESS_IS_MISSING);

        // Check bizKey is required when using template messages
        assertTrue(!email.isTemplateEmail()
            || nonNull(email.getBizKey()), TEMPLATE_BIZ_KEY_NOT_NULL);

        if (!isUserAction()) {
          assertTrue(nonNull(email.getSendTenantId()), "sendTenantId is required");
          assertTrue(nonNull(email.getSendUserId()), "sendUserId is required");
        }

        if (testServer) {
          emailServerDb = emailServerQuery.checkAndFind(email.getServerId());
        } else {
          emailServerDb = emailServerQuery.findEnabled(EmailProtocol.SMTP);
        }

        // Check the max number of attachment
        emailQuery.checkAttachmentQuota(email);

        // Check template existed
        if (email.isTemplateEmail()) {
          emailTemplateDb = emailQuery.checkAndFindTemplate(email);
        }
      }

      @Override
      protected Void process() {
        // Sending by toAddress has higher priority than sending by orgType
        if (email.isSendByToAddress()) {
          send0(testServer, email, emailTemplateDb, emailServerDb);
        } else {
          // When both type exist, sending by orgType will be ignored
          int page = 0, size = 500;
          List<String> pageEmails = getReceiveObjectEmails(email.getReceiveObjectType(),
              email.getReceiveObjectIds(), email.getReceivePolicyCodes(), page, size);
          if (isEmpty(pageEmails)) {
            log.warn("The receiver's email address is not found, email: {}", toJson(email));
            return null;
          }
          do {
            // If sending by orgType, it will be transferred to sending by mobiles
            email.setToAddrData(new HashSet<>(pageEmails));
            send0(testServer, email, emailTemplateDb, emailServerDb);
            if (!email.getReceiveObjectType().equals(ReceiveObjectType.USER)) {
              pageEmails = getReceiveObjectEmails(email.getReceiveObjectType(),
                  email.getReceiveObjectIds(), email.getReceivePolicyCodes(), ++page, size);
              if (isNotEmpty(pageEmails)) {
                // If sending by type requires multiple times, generate a new split EMAIL ID
                email.setId(uidGenerator.getUID());
              }
            } else {
              // Query only once, max to 500
              pageEmails = null;
            }
          } while (nonNull(pageEmails) && !pageEmails.isEmpty());
        }
        return null;
      }
    }.execute();
  }

  @DoInFuture("Support resending after failure")
  @Override
  public void sendByJob(Email email) {
    new BizTemplate<Void>() {
      @SneakyThrows
      @Override
      protected Void process() {
        EmailServer emailServerDb = emailServerQuery.findEnabled(EmailProtocol.SMTP);
        if (email.isTemplateEmail()) {
          send0(false, email, emailQuery.checkAndFindTemplate(email), emailServerDb);
        } else {
          send0(false, email, null, emailServerDb);
        }
        return null;
      }
    }.execute();
  }

  @Override
  public void checkVerificationCode(EmailBizKey bizKey, String email, String verificationCode) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        String cacheKey = getVerificationCodeCacheKey(bizKey, email);
        String cachedVc = stringRedisService.get(cacheKey);
        assertNotEmpty(cachedVc, VERIFY_CODE_EXPIRED);
        assertTrue(equalsIgnoreCase(verificationCode, cachedVc), VERIFY_CODE_ERROR);
        deleteVerificationCodeCache(cacheKey, bizKey, email);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(HashSet<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        emailRepo.deleteByIdIn(ids);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update0(List<Email> emails) {
    batchUpdate0(emails);
  }

  @Transactional(rollbackFor = Exception.class, noRollbackFor = NoRollbackException.class)
  public void send0(boolean serverTest, Email email, EmailTemplate template,
      EmailServer enabledServer) {
    if (!isJob()) {
      // By api has been executed when calling to prevent repeated execution.
      assembleEmailSendParam(serverTest, email, template, enabledServer);
    }

    email.setId(Objects.isNull(email.getId()) ? uidGenerator.getUID() : email.getId());
    if (email.isTemplateEmail()) {
      email.setTemplateCode(template.getCode());
    }
    // Retry is unsupported
    email.setSendRetryNum(0);

    // ** Send now **
    if (serverTest || email.getVerificationCode() || email.isSendNow()) {
      try {
        // Send urgent email directly
        sendEmailNow(email, template, enabledServer);
        email.setActualSendDate(LocalDateTime.now());
        emailRepo.save(email);

        // Cache verification code when it is sent successfully
        if (email.getVerificationCode()) {
          cacheVerificationCode(email, template);
        }
        return;
      } catch (Exception e) {
        log.error("Sending email exception, email: {}", GsonUtils.toJson(email));
        log.error("Cause: ", e);
        email.setSendStatus(ProcessStatus.FAILURE).setFailureReason(e.getMessage());
        // insert0(email); // Fix:: When sending again, it should be updated rather than inserted
        emailRepo.save(email);

        throw translateSendNoRollbackException(e, email);
      }
    }

    // ** Send in future by job **
    // Save and wait for timing sending of job
    email.setSendStatus(ProcessStatus.PENDING);
    // Makes it possible to query it when the job is sent
    if (isNull(email.getExpectedSendDate())) {
      email.setExpectedSendDate(LocalDateTime.now());
    }
    emailRepo.save(email);
  }

  private void sendEmailNow(Email email, EmailTemplate template, EmailServer enabledServer) {
    if (!email.getBatch()) {
      // Fix :: All platform users must send one email to one address
      for (String toAddr : email.getToAddrData()) {
        email.setActualToAddrData(Collections.singleton(toAddr));
        try {
          if (!email.isTemplateEmail() || isEmpty(email.getTemplateParamData())
              || email.getTemplateParamData().size() <= 1) {
            emailSender.sendMessage(enabledServer, template, email);
          } else {
            emailSender.sendBatchMessage(enabledServer, template, email);
          }
          email.setSendStatus(ProcessStatus.SUCCESS);
        } catch (Exception e) {
          // Allow exception when send from all platform users
          // Fix:: Sending failed because of invalid destination addresses or rate limiting is triggered too frequently
          email.setSendStatus(ProcessStatus.FAILURE).setFailureReason(e.getMessage());
          log.error("Send email {} to address {} exception: {}",
              email.getId(), email.getActualToAddrData().toString(), e.getMessage());
        }
      }
    } else {
      email.setActualToAddrData(email.getToAddrData());
      try {
        if (!email.isTemplateEmail() || isEmpty(email.getTemplateParamData())
            || email.getTemplateParamData().size() <= 1) {
          emailSender.sendMessage(enabledServer, template, email);
        } else {
          emailSender.sendBatchMessage(enabledServer, template, email);
        }
        email.setSendStatus(ProcessStatus.SUCCESS);
      } catch (Exception e) {
        email.setSendStatus(ProcessStatus.FAILURE).setFailureReason(e.getMessage());
      }
    }
  }

  /**
   * @param receiveObjectType  {@link ReceiveObjectType}
   * @param receiveObjectIds   receive object ids
   * @param receivePolicyCodes receive policy codes
   * @param page               zero-based page index, must not be negative.
   * @param size               the size of the page to be returned, must be greater than 0.
   * @return mobiles
   */
  public List<String> getReceiveObjectEmails(ReceiveObjectType receiveObjectType,
      List<Long> receiveObjectIds, List<String> receivePolicyCodes, int page, int size) {
    List<String> pageEmails = null;
    switch (receiveObjectType) {
      case TENANT:
        pageEmails = userRepo.findValidEmailByTenantIdIn(receiveObjectIds,
            PageRequest.of(page, size, Sort.Direction.DESC, "id")).getContent();
        break;
      case USER:
        pageEmails = userRepo.findValidEmailByIdIn(receiveObjectIds);
        break;
      case DEPT:
        pageEmails = deptUserRepo.findValidEmailByDeptIds(receiveObjectIds, page * size, size);
        break;
      case GROUP:
        pageEmails = groupUserRepo.findValidEmailByGroupIds(receiveObjectIds, page * size, size);
        break;
      case POLICY:
        // TODO
        break;
      case TO_POLICY:
        pageEmails = toRoleUserRepo
            .findValidEmailByRoleCodes(receivePolicyCodes, page * size, size);
        break;
      case ALL:
        pageEmails = userRepo
            .findValidAllEmail(PageRequest.of(page, size, Sort.Direction.DESC, "id")).getContent();
        break;
      default:
        // NOOP
    }
    return Objects.nonNull(pageEmails) ? pageEmails.stream()
        .filter(ObjectUtils::isNotEmpty).collect(Collectors.toList()) : null;
  }

  private void cacheVerificationCode(Email email, EmailTemplate template) {
    email.getToAddrData().forEach(e -> {
      stringRedisService.set(getVerificationCodeCacheKey(email.getBizKey(), e),
          String.valueOf(email.getTemplateParamData().get(e).get(VC_PARAM_NAME)),
          getVerificationCodeValidSecond(email, template), TimeUnit.SECONDS);
      stringRedisService.set(getVerificationCodeRepeatCheckKey(email.getBizKey(), e),
          String.valueOf(email.getTemplateParamData().get(e).get(VC_PARAM_NAME)),
          VC_REPEAT_SEND_LIMIT_SECOND, TimeUnit.SECONDS);
    });
  }

  private void deleteVerificationCodeCache(String cacheKey, EmailBizKey bizKey, String email) {
    stringRedisService.delete(cacheKey);
    stringRedisService.delete(getVerificationCodeRepeatCheckKey(bizKey, email));
  }

  /**
   * Translation basic mail sending exception
   */
  private NoRollbackException translateSendNoRollbackException(Exception e, Email email) {
    /*if (e instanceof MailConnectException) {
      return NoRollbackException.of(CONNECT_EXCEPTION_CODE, CONNECT_EXCEPTION);
    } else */
    if (e instanceof MailSendException) {
      return NoRollbackException.of(SEND_EXCEPTION_CODE, e.getMessage());
    } else if (e instanceof MessagingException) {
      return NoRollbackException.of(SEND_EXCEPTION_CODE, e.getMessage());
    } else if (e instanceof IOException) {
      return NoRollbackException.of(TEMPLATE_IO_EXCEPTION_CODE, TEMPLATE_IO_EXCEPTION,
          new Object[]{email.getTemplateCode()});
    } else if (e instanceof TemplateException) {
      return NoRollbackException.of(TEMPLATE_PARSE_EXCEPTION_CODE, TEMPLATE_PARSE_EXCEPTION,
          new Object[]{email.getTemplateCode()});
    }
    return NoRollbackException.of(SEND_EXCEPTION_CODE, e.getMessage());
  }

  private void assembleEmailSendParam(boolean serverTest, Email email, EmailTemplate template,
      EmailServer emailServer) {
    if (isEmpty(email.getFromAddr()) && nonNull(emailServer.getAuthAccountData())) {
      email.setFromAddr(emailServer.getAuthAccountData().getAccount());
    }
    if (isEmpty(email.getSubject())) {
      email.setSubject(template.getSubject());
    }
    if (isNotBlank(emailServer.getSubjectPrefix())
        && !email.getSubject().startsWith(emailServer.getSubjectPrefix())) {
      email.setSubject(emailServer.getSubjectPrefix() + "|" + email.getSubject());
    }
    if (serverTest) {
      assembleTestChannelParams(email, emailServer);
    }
    if (email.getVerificationCode()) {
      assembleVerificationCodeParams(email);
    }
    if (email.isTemplateEmail()) {
      email.setTemplateCode(template.getTemplateBiz().getTemplateCode());
    }
    if (!isUserAction()) {
      email.setCreatedBy(email.getSendUserId()).setLastModifiedBy(email.getSendUserId());
    }
    // Enforce only one email receiving address
    if (email.isSendByAllPlatformUsers() || email.isTemplateEmail()) {
      email.setBatch(false);
    }
  }

  private void assembleTestChannelParams(Email email, EmailServer emailServer) {
    email.setTemplateParamData(null);
    Map<String, Map<String, String>> testChannelTemplateParam = new HashMap<>();
    for (String em : email.getToAddrData()) {
      testChannelTemplateParam.put(em, new HashMap<>());
      testChannelTemplateParam.get(em).put(MAIL_CHANNEL_VARIABLE_PREFIX, emailServer.getName());
      email.setTemplateParamData(testChannelTemplateParam);
    }
    email.setTemplateParamData(testChannelTemplateParam);
  }

  /**
   * Build verification code parameters
   */
  private void assembleVerificationCodeParams(Email email) {
    email.setTemplateParamData(null);
    Map<String, Map<String, String>> vcTemplateParam = new HashMap<>();
    for (String em : email.getToAddrData()) {
      vcTemplateParam.put(em, new HashMap<>());
      vcTemplateParam.get(em).put(VC_VARIABLE, randomNumeric(6));
    }
    email.setTemplateParamData(vcTemplateParam);
  }

  @Override
  protected BaseRepository<Email, Long> getRepository() {
    return this.emailRepo;
  }

}
