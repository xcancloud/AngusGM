package cloud.xcan.angus.core.gm.application.query.sms.impl;

import static cloud.xcan.angus.core.biz.BizAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.converter.SmsConverter.getVerificationCodeRepeatCheckKey;
import static cloud.xcan.angus.core.gm.domain.SmsMessage.NO_TEMPLATE_BIZ_CONFIG_CODE;
import static cloud.xcan.angus.core.gm.domain.SmsMessage.NO_TEMPLATE_BIZ_CONFIG_T;
import static cloud.xcan.angus.core.gm.domain.SmsMessage.SMS_CHANNEL_NOT_AVAILABLE;
import static cloud.xcan.angus.core.gm.domain.SmsMessage.SMS_CHANNEL_NOT_AVAILABLE_CODE;
import static cloud.xcan.angus.core.gm.domain.SmsMessage.SMS_NO_PLUGIN_ERROR;
import static cloud.xcan.angus.core.gm.domain.SmsMessage.SMS_NO_PLUGIN_ERROR_CODE;
import static cloud.xcan.angus.core.gm.domain.SmsMessage.VERIFY_CODE_SEND_REPEATED;
import static cloud.xcan.angus.core.gm.domain.SmsMessage.VERIFY_CODE_SEND_REPEATED_CODE;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.enums.ProcessStatus;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.sms.SmsChannelQuery;
import cloud.xcan.angus.core.gm.application.query.sms.SmsQuery;
import cloud.xcan.angus.core.gm.domain.channel.SmsChannel;
import cloud.xcan.angus.core.gm.domain.sms.InputParam;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.domain.sms.SmsRepo;
import cloud.xcan.angus.core.gm.domain.sms.biz.SmsTemplateBiz;
import cloud.xcan.angus.core.gm.domain.sms.biz.SmsTemplateBizRepo;
import cloud.xcan.angus.core.gm.domain.sms.template.SmsTemplate;
import cloud.xcan.angus.core.gm.domain.sms.template.SmsTemplateRepo;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.core.spring.SpringContextHolder;
import cloud.xcan.angus.core.utils.ValidatorUtils;
import cloud.xcan.angus.extension.sms.api.SmsProvider;
import cloud.xcan.angus.lettucex.util.RedisService;
import cloud.xcan.angus.remote.message.SysException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


@Biz
@SummaryQueryRegister(name = "Sms", table = "sms", isMultiTenantCtrl = false,
    groupByColumns = {"actual_send_date", "send_status", "urgent", "verification_code",
        "batch"})
public class SmsQueryImpl implements SmsQuery {

  @Resource
  private SmsRepo smsRepo;

  @Resource
  private SmsChannelQuery smsChannelQuery;

  @Resource
  private SmsTemplateRepo smsTemplateRepo;

  @Resource
  private SmsTemplateBizRepo smsTemplateBizRepo;

  @Resource
  private RedisService<String> stringRedisService;

  @Override
  public Sms find(Long id) {
    return new BizTemplate<Sms>() {

      @Override
      protected Sms process() {
        return smsRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(String.valueOf(id), "Sms"));
      }
    }.execute();
  }

  @Override
  public Page<Sms> find(Specification<Sms> spec, Pageable pageable) {
    return new BizTemplate<Page<Sms>>() {

      @Override
      protected Page<Sms> process() {
        return smsRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public List<Sms> findSmsInPending(int count) {
    return smsRepo.findAllBySendStatusAndSize(ProcessStatus.PENDING.getValue(), count);
  }

  @Override
  public void checkVerifyCodeSendRepeated(Sms sms) {
    final InputParam inputParamData = sms.getInputParamData();
    inputParamData.getMobiles().forEach(mobile -> {
          String cachedVc = stringRedisService.get(
              getVerificationCodeRepeatCheckKey(inputParamData.getBizKey(), mobile));
          assertTrue(isEmpty(cachedVc), VERIFY_CODE_SEND_REPEATED_CODE, VERIFY_CODE_SEND_REPEATED);
        }
    );
  }

  @Override
  public void checkMobileFormat(Sms sms) {
    if (sms.isSendByMobiles()) {
      sms.getInputParamData().getMobiles().forEach(ValidatorUtils::checkMobile);
    }
  }

  @Override
  public SmsChannel checkChannelEnabledAndGet() {
    SmsChannel enabledSmsChannel = smsChannelQuery.findEnabled();
    if (isNull(enabledSmsChannel)) {
      throw SysException.of(SMS_CHANNEL_NOT_AVAILABLE_CODE, SMS_CHANNEL_NOT_AVAILABLE);
    }
    return enabledSmsChannel;
  }

  @Override
  public SmsTemplate checkTemplateAndGet(Sms sms, SmsChannel enabledSmsChannel) {
    SmsTemplate smsTemplate = null;
    SmsTemplateBiz smsTemplateBiz = smsTemplateBizRepo.findByBizKey(
        sms.getInputParamData().getBizKey());
    if (nonNull(smsTemplateBiz)) {
      smsTemplate = smsTemplateRepo.findByCodeAndLanguageAndChannelId(
          smsTemplateBiz.getTemplateCode(),
          sms.getLanguage(), enabledSmsChannel.getId());
    }
    if (isNull(smsTemplateBiz) || isNull(smsTemplate)) {
      throw SysException.of(NO_TEMPLATE_BIZ_CONFIG_CODE, NO_TEMPLATE_BIZ_CONFIG_T,
          new Object[]{sms.getInputParamData().getBizKey()});
    }
    return smsTemplate;
  }

  @Override
  public SmsProvider checkAndGetSmsProvider(SmsChannel enabledSmsChannel) {
    SmsProvider enabledSmsProvider = null;
    Map<String, SmsProvider> providerMap = null;
    try {
      //Fix:: Dependency injection On linux
      providerMap = SpringContextHolder.getCtx().getBeansOfType(SmsProvider.class);
    } catch (Exception e) {
      // NOOP
    }
    if (nonNull(providerMap) && isNotEmpty(providerMap.values())) {
      for (SmsProvider provider : providerMap.values()) {
        if (nonNull(provider.getInstallationChannel()) && provider.getInstallationChannel()
            .getName().equalsIgnoreCase(enabledSmsChannel.getName())) {
          enabledSmsProvider = provider;
          break;
        }
      }
    }
    if (isNull(enabledSmsProvider)) {
      // No SMS plugin available
      throw SysException.of(SMS_NO_PLUGIN_ERROR_CODE, SMS_NO_PLUGIN_ERROR);
    }
    return enabledSmsProvider;
  }

}
