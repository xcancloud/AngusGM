package cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler;

import static cloud.xcan.angus.api.commonlink.SmsConstants.VC_PARAM_NAME;
import static cloud.xcan.angus.spec.locale.SdfLocaleHolder.getLocale;
import static cloud.xcan.angus.spec.locale.SupportedLanguage.safeLanguage;
import static cloud.xcan.angus.spec.utils.ObjectUtils.emptySafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.SmsConstants;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.enums.ProcessStatus;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.api.gm.sms.dto.SmsSendDto;
import cloud.xcan.angus.core.gm.domain.sms.InputParam;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.SignSmsSendDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.channel.SmsChannelTestSendDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsDetailVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsInputParamVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentSmsSendDto;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;


public class SmsAssembler {

  public static Sms dtoToDomain(SmsSendDto dto) {
    Sms sms = new Sms()
        .setBizKey(dto.getBizKey())
        .setLanguage(nonNull(dto.getLanguage()) ? dto.getLanguage()
            : safeLanguage(getLocale()))
        .setOutId(emptySafe(dto.getOutId(), "INNER-" + UUID.randomUUID()))
        .setExpectedSendDate(dto.getExpectedSendDate())
        .setUrgent(nonNull(dto.getUrgent()) ? dto.getUrgent() : false)
        .setVerificationCode(dto.getVerificationCode())
        .setSendStatus(ProcessStatus.FAILURE)
        .setThirdInputParam("")
        .setThirdOutputParam("")
        .setInputParamData(new InputParam().setMobiles(
                isNotEmpty(dto.getMobiles()) ? new HashSet<>(dto.getMobiles()) : null)
            .setBizKey(dto.getBizKey())
            .setExpire(dto.getVerificationCodeValidSecond())
            .setTemplateParams(dto.getTemplateParams()))
        .setSendTenantId(PrincipalContext.getTenantId())
        .setSendUserId(PrincipalContext.getUserId())
        .setReceiveObjectType(dto.getReceiveObjectType())
        .setReceiveObjectIds(dto.getReceiveObjectIds())
        .setReceivePolicyCodes(dto.getReceivePolicyCodes());
    sms.setBatch((sms.isSendByMobiles() && dto.getMobiles().size() > 1)
        || sms.isSendByOrgType());
    return sms;
  }

  public static Sms signToDomain(SignSmsSendDto dto) {
    return new Sms()
        .setBizKey(dto.getBizKey())
        .setLanguage(safeLanguage(getLocale()))
        .setOutId("INNER-" + UUID.randomUUID())
        .setExpectedSendDate(null)
        .setUrgent(true)
        .setVerificationCode(true)
        .setBatch(false)
        .setSendStatus(ProcessStatus.PENDING)
        .setThirdInputParam("")
        .setThirdOutputParam("")
        .setInputParamData(new InputParam().setMobiles(Set.of(dto.getMobile()))
            .setBizKey(dto.getBizKey())
            .setExpire(SmsConstants.DEFAULT_VC_VALID_SECOND)
            .setTemplateParams(Map.of("action", dto.getBizKey().getValue())))
        .setSendTenantId(PrincipalContext.getTenantId())
        .setSendUserId(PrincipalContext.getUserId())
        .setReceiveObjectType(ReceiveObjectType.USER)
        .setReceiveObjectIds(null)
        .setReceivePolicyCodes(null);
  }

  public static Sms signToDomain(CurrentSmsSendDto dto) {
    return new Sms()
        .setBizKey(dto.getBizKey())
        .setLanguage(safeLanguage(getLocale()))
        .setOutId("INNER-" + UUID.randomUUID())
        .setExpectedSendDate(null)
        .setUrgent(true)
        .setVerificationCode(true)
        .setBatch(false)
        .setSendStatus(ProcessStatus.PENDING)
        .setThirdInputParam("")
        .setThirdOutputParam("")
        .setInputParamData(new InputParam().setMobiles(Set.of(dto.getMobile()))
            .setBizKey(dto.getBizKey())
            .setExpire(SmsConstants.DEFAULT_VC_VALID_SECOND)
            .setTemplateParams(Map.of("action", dto.getBizKey().getValue())))
        .setSendTenantId(PrincipalContext.getTenantId())
        .setSendUserId(PrincipalContext.getUserId())
        .setReceiveObjectType(ReceiveObjectType.USER)
        .setReceiveObjectIds(null)
        .setReceivePolicyCodes(null);
  }

  public static Sms channelTestSendDtoToDomain(SmsChannelTestSendDto dto) {
    return new Sms()
        .setBizKey(SmsBizKey.CHANNEL_TEST)
        .setLanguage(safeLanguage(getLocale()))
        .setOutId("INNER-" + UUID.randomUUID())
        .setUrgent(true)
        .setChannelId(dto.getChannelId())
        .setBatch(dto.getMobiles().size() > 1)
        .setVerificationCode(false)
        .setSendStatus(ProcessStatus.FAILURE)
        .setThirdInputParam("")
        .setThirdOutputParam("")
        .setInputParamData(
            new InputParam().setMobiles(Sets.newHashSet(dto.getMobiles()))
                .setBizKey(SmsBizKey.CHANNEL_TEST).setExpire(null))
        .setSendTenantId(PrincipalContext.getTenantId())
        .setSendUserId(PrincipalContext.getUserId());
  }

  public static SmsDetailVo toDetailVo(Sms sms) {
    return new SmsDetailVo().setId(sms.getId())
        .setTemplateCode(sms.getTemplateCode())
        .setLanguage(sms.getLanguage())
        .setBizKey(sms.getBizKey())
        .setOutId(sms.getOutId())
        .setVerificationCode(sms.getVerificationCode())
        .setBatch(sms.getBatch())
        .setUrgent(sms.getUrgent())
        .setActualSendDate(sms.getActualSendDate())
        .setExpectedSendDate(sms.getExpectedSendDate())
        .setSendStatus(sms.getSendStatus())
        .setThirdOutputParam(sms.getThirdOutputParam())
        .setInputParam(toInputParamVo(sms.getInputParamData()))
        .setSendUserId(sms.getSendUserId())
        .setSendTenantId(sms.getSendTenantId());
  }

  public static SmsInputParamVo toInputParamVo(InputParam inputParam) {
    if (isNotEmpty(inputParam.getTemplateParams())
        && inputParam.getTemplateParams().containsKey(VC_PARAM_NAME)) {
      inputParam.getTemplateParams().put(VC_PARAM_NAME, "******");
    }
    return new SmsInputParamVo().setMobiles(inputParam.getMobiles())
        .setTemplateParams(inputParam.getTemplateParams())
        .setExpire(inputParam.getExpire())
        .setBizKey(inputParam.getBizKey());
  }

  public static Specification<Sms> getSpecification(SmsFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .orderByFields("id", "actualSendDate", "expectedSendDate")
        .rangeSearchFields("id", "actualSendDate", "expectedSendDate")
        .build();
    return new GenericSpecification<>(filters);
  }
}
