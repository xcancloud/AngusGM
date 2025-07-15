package cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler;

import static cloud.xcan.angus.api.commonlink.EmailConstant.VC_PARAM_NAME;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isUserAction;
import static cloud.xcan.angus.spec.locale.SdfLocaleHolder.getLocale;
import static cloud.xcan.angus.spec.locale.SupportedLanguage.safeLanguage;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.emptySafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.EmailConstant;
import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.email.EmailType;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.api.gm.email.dto.EmailSendDto;
import cloud.xcan.angus.api.pojo.Attachment;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.sign.SignEmailSendDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTestDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailDetailVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentEmailSendDto;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;

public class EmailAssembler {

  public static Email toDomain(EmailSendDto dto) {
    return new Email()
        .setBizKey(dto.getBizKey())
        .setLanguage(nonNull(dto.getLanguage())
            ? safeLanguage(dto.getLanguage()) : safeLanguage(getLocale()))
        .setType(dto.getType())
        .setOutId(emptySafe(dto.getOutId(), genDefaultOutId()))
        .setSubject(stringSafe(dto.getSubject()))
        .setToAddrData(dto.getToAddress())
        .setReceiveObjectType(dto.getReceiveObjectType())
        .setReceiveObjectIds(dto.getReceiveObjectIds())
        .setReceivePolicyCodes(dto.getReceivePolicyCodes())
        .setCcAddrData(dto.getCcAddress())
        .setVerificationCode(dto.getVerificationCode())
        .setVerificationCodeValidSecond(dto.getVerificationCodeValidSecond())
        .setHtml(dto.getHtml())
        .setContent(dto.getContent())
        .setTemplateParamData(dto.getTemplateParams())
        .setAttachmentData(toAttachmentDomain(dto.getAttachments()))
        .setExpectedSendDate(dto.getExpectedSendDate())
        .setBatch(nullSafe(dto.getBatch(), true))
        .setUrgent(!Objects.isNull(dto.getUrgent()) && dto.getUrgent())
        .setSendTenantId(isUserAction() ? getTenantId()
            : nullSafe(dto.getSendTenantId(), -1L/* Is null when signup */))
        .setSendUserId(isUserAction() ? getUserId()
            : nullSafe(dto.getSendUserId(), -1L/* Is null when signup */));
  }

  public static Email signToDomain(SignEmailSendDto dto) {
    return new Email()
        .setBizKey(dto.getBizKey())
        .setLanguage(safeLanguage(getLocale()))
        .setType(EmailType.TEMPLATE)
        .setOutId(genDefaultOutId())
        .setSubject("")
        .setToAddrData(dto.getToAddress())
        .setReceiveObjectType(ReceiveObjectType.USER)
        .setReceiveObjectIds(null)
        .setReceivePolicyCodes(null)
        .setCcAddrData(null)
        .setVerificationCode(true)
        .setVerificationCodeValidSecond(EmailConstant.DEFAULT_VC_VALID_SECOND)
        .setHtml(false)
        .setContent(null)
        .setTemplateParamData(null)
        .setAttachmentData(null)
        .setExpectedSendDate(null)
        .setBatch(false)
        .setUrgent(true)
        .setSendTenantId(getTenantId()) // Invoke by message service remote
        .setSendUserId(getTenantId()); // Invoke by message service remote
  }

  public static Email signToDomain(CurrentEmailSendDto dto) {
    return new Email()
        .setBizKey(dto.getBizKey())
        .setLanguage(SupportedLanguage.safeLanguage(getLocale()))
        .setType(EmailType.TEMPLATE)
        .setOutId(genDefaultOutId())
        .setSubject("")
        .setToAddrData(dto.getToAddress())
        .setReceiveObjectType(ReceiveObjectType.USER)
        .setReceiveObjectIds(null)
        .setReceivePolicyCodes(null)
        .setCcAddrData(null)
        .setVerificationCode(true)
        .setVerificationCodeValidSecond(EmailConstant.DEFAULT_VC_VALID_SECOND)
        .setHtml(false)
        .setContent(null)
        .setTemplateParamData(null)
        .setAttachmentData(null)
        .setExpectedSendDate(null)
        .setBatch(false)
        .setUrgent(true)
        .setSendTenantId(getTenantId()) // Invoke by message service remote
        .setSendUserId(getTenantId()); // Invoke by message service remote
  }

  public static Email testToDomain(EmailTestDto dto) {
    return new Email()
        .setServerId(dto.getServerId())
        .setBizKey(EmailBizKey.CHANNEL_TEST)
        .setLanguage(safeLanguage(getLocale()))
        .setOutId(genDefaultOutId())
        .setType(EmailType.TEMPLATE)
        .setToAddrData(dto.getToAddress())
        .setVerificationCode(false)
        .setHtml(true)
        .setBatch(false)
        .setUrgent(true)
        .setSendTenantId(isUserAction() ? getTenantId()
            : nullSafe(dto.getSendTenantId(), -1L/* Is null when signup */))
        .setSendUserId(isUserAction() ? getUserId()
            : nullSafe(dto.getSendUserId(), -1L/* Is null when signup */));
  }

  public static EmailDetailVo toDetailVo(Email email) {
    return new EmailDetailVo().setId(email.getId())
        .setTemplateCode(email.getTemplateCode())
        .setBizKey(email.getBizKey())
        .setLanguage(email.getLanguage())
        .setEmailType(email.getType())
        .setOutId(email.getOutId())
        .setSubject(email.getSubject())
        .setFromAddr(email.getFromAddr())
        .setToAddress(email.getToAddrData())
        .setCcAddress(email.getCcAddrData())
        .setVerificationCode(email.getVerificationCode())
        .setVerificationCodeValidSecond(email.getVerificationCodeValidSecond())
        .setHtml(email.getHtml())
        .setSendStatus(email.getSendStatus())
        .setFailureReason(email.getFailureReason())
        // Do not return the content of the verification code
        .setContent(email.getVerificationCode() ? "" : email.getContent())
        .setTemplateCode(email.getTemplateCode())
        .setTemplateParams(desensitization(email.getTemplateParamData()))
        .setAttachments(toAttachment(email.getAttachmentData()))
        .setActualSendDate(email.getActualSendDate())
        .setExpectedSendDate(email.getExpectedSendDate())
        .setUrgent(email.getUrgent())
        .setSendTenantId(email.getSendTenantId())
        .setBatch(email.getBatch())
        .setCreatedDate(email.getCreatedDate());
  }

  public static Map<String, Map<String, String>> desensitization(
      Map<String, Map<String, String>> templateParamData) {
    if (isEmpty(templateParamData) || isEmpty(templateParamData.values())) {
      return null;
    }
    for (Map<String, String> value : templateParamData.values()) {
      if (value.containsKey(VC_PARAM_NAME)) {
        value.put(VC_PARAM_NAME, "******");
      }
    }
    return templateParamData;
  }

  public static String genDefaultOutId() {
    return "INNER-" + UUID.randomUUID();
  }

  public static Set<Attachment> toAttachmentDomain(Set<Attachment> tos) {
    return isEmpty(tos) ? null : tos.stream().map(to ->
        new Attachment().setName(to.getName()).setUrl(to.getUrl())
    ).collect(Collectors.toSet());
  }

  public static Set<Attachment> toAttachment(Set<Attachment> data) {
    return isEmpty(data) ? null : data.stream().map(a ->
        new Attachment().setName(a.getName()).setUrl(a.getUrl())
    ).collect(Collectors.toSet());
  }

  public static Specification<Email> getSpecification(EmailFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .orderByFields("id", "actualSendDate", "expectedSendDate")
        .rangeSearchFields("id", "actualSendDate", "expectedSendDate")
        .build();
    return new GenericSpecification<>(filters);
  }
}
