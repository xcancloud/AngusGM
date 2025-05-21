package cloud.xcan.angus.api.manager.converter;

import static cloud.xcan.angus.spec.experimental.BizConstant.OWNER_TENANT_ID;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserFullName;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.emptySafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.email.EmailType;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.api.gm.email.dto.EmailSendDto;
import cloud.xcan.angus.api.gm.message.dto.MessageCenterPushDto;
import cloud.xcan.angus.api.gm.notice.dto.SendEmailParam;
import cloud.xcan.angus.api.gm.notice.dto.SendInsiteParam;
import cloud.xcan.angus.api.gm.notice.dto.SendSmsParam;
import cloud.xcan.angus.api.gm.sms.dto.SmsSendDto;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import java.util.List;
import java.util.Map;


public class NoticeConverter {

  /**
   * Used by UC/Workorder/AAS.
   */
  public static SendSmsParam toSendTemplateSmsParam(SmsBizKey bizKey, String outId,
      ReceiveObjectType receiveObjectType, List<Long> receiveUserIds,
      Map<String, String> templateParams) {
    return SendSmsParam.newBuilder()
        .bizKey(bizKey)
        .outId(outId)
        /*.language(SupportedLanguage.zh_CN.getValue())*/
        .templateParams(templateParams)
        .receiveObjectIds(receiveUserIds)
        .receiveObjectType(receiveObjectType)
        .build();
  }

  public static SendSmsParam toSendTopPolicyTemplateSmsParam(SmsBizKey bizKey, String outId,
      List<String> receivePolicyCodes, Map<String, String> templateParams) {
    return SendSmsParam.newBuilder()
        .bizKey(bizKey)
        .outId(outId)
        /*.language(SupportedLanguage.zh_CN.getValue())*/
        .templateParams(templateParams)
        .receivePolicyCodes(receivePolicyCodes)
        .receiveObjectType(ReceiveObjectType.TO_POLICY)
        .build();
  }

  public static SmsSendDto toSmsSendDto(SendSmsParam param) {
    return new SmsSendDto()
        .setOutId(param.getOutId())
        .setUrgent(param.getUrgent())
        .setLanguage(SupportedLanguage.safeLanguage(param.getLanguage()))
        .setBizKey(param.getBizKey())
        .setMobiles(param.getMobiles())
        .setVerificationCode(false)
        .setTemplateParams(param.getTemplateParams())
        .setReceiveObjectType(param.getReceiveObjectType())
        .setReceiveObjectIds(param.getReceiveObjectIds())
        .setReceivePolicyCodes(param.getReceivePolicyCodes());
  }

  /**
   * Used by UC/Workorder/AAS.
   */
  public static SendEmailParam toSendTemplateEmailParam(EmailBizKey bizKey, String outId,
      ReceiveObjectType receiveObjectType, List<Long> receiveUserIds,
      Map<String, Map<String, String>> templateParams) {
    return SendEmailParam.newBuilder()
        .bizKey(bizKey)
        .outId(outId)
        .templateParams(templateParams)
        .type(EmailType.TEMPLATE)
        .html(true)
        .verificationCode(false)
        /*.language(SupportedLanguage.zh_CN.getValue())*/
        .sendTenantId(OWNER_TENANT_ID)
        .sendUserId(-1L)
        .receiveObjectIds(receiveUserIds)
        .receiveObjectType(receiveObjectType)
        .build();
  }

  public static SendEmailParam toSendTopPolicyTemplateEmailParam(EmailBizKey bizKey, String outId,
      List<String> receivePolicyCodes, Map<String, Map<String, String>> templateParams) {
    return SendEmailParam.newBuilder()
        .bizKey(bizKey)
        .outId(outId)
        .templateParams(templateParams)
        .type(EmailType.TEMPLATE).html(true)
        .verificationCode(false)
        /*.language(SupportedLanguage.zh_CN.getValue())*/
        .sendTenantId(OWNER_TENANT_ID)
        .sendUserId(-1L)
        .receivePolicyCodes(receivePolicyCodes)
        .receiveObjectType(ReceiveObjectType.TO_POLICY)
        .build();
  }

  public static EmailSendDto toEmailSendDto(SendEmailParam param) {
    return new EmailSendDto()
        .setOutId(param.getOutId())
        .setUrgent(false)
        .setLanguage(SupportedLanguage.safeLanguage(param.getLanguage()).getValue())
        .setType(param.getType())
        .setBizKey(param.getBizKey())
        .setContent(param.getContent())
        .setSubject(param.getSubject())
        .setVerificationCode(param.getVerificationCode())
        .setVerificationCodeValidSecond(param.getVerificationCodeValidSecond())
        .setSendTenantId(param.getSendTenantId())
        .setSendUserId(param.getSendUserId())
        .setTemplateParams(param.getTemplateParams())
        .setAttachments(param.getAttachments())
        .setHtml(param.getHtml())
        .setReceiveObjectType(param.getReceiveObjectType())
        .setReceiveObjectIds(param.getReceiveObjectIds())
        .setReceivePolicyCodes(param.getReceivePolicyCodes())
        .setCcAddress(param.getCcAddress())
        .setToAddress(param.getToAddress())
        .setType(param.getType());
  }

  public static MessageCenterPushDto toMsgCenterPushDto(SendInsiteParam param) {
    return new MessageCenterPushDto()
        .setMediaType(param.getPushMediaType())
        .setSendBy(nullSafe(param.getSendUserId(), getUserId()))
        .setSendByName(nullSafe(param.getSendUserName(), emptySafe(getUserFullName(), "System")))
        .setMessageId(param.getMessageId())
        .setTitle(param.getTitle())
        .setContent(param.getContent())
        .setReceiveObjectType(param.getReceiveObjectType())
        .setReceiveObjectIds(param.getReceiveObjectIds());
  }

}
