package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.spec.locale.SdfLocaleHolder.getLocale;
import static cloud.xcan.angus.spec.locale.SupportedLanguage.safeLanguage;

import cloud.xcan.angus.api.commonlink.SmsConstants;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.enums.ProcessStatus;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.core.gm.domain.sms.InputParam;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TenantSignSmsConverter {

  public static Sms toCancelConfirmSmsDto(String mobile) {
    return new Sms()
        .setBizKey(SmsBizKey.SIGN_CANCEL)
        .setLanguage(safeLanguage(getLocale()))
        .setOutId("INNER-" + UUID.randomUUID())
        .setExpectedSendDate(null)
        .setUrgent(true)
        .setVerificationCode(true)
        .setBatch(false)
        .setSendStatus(ProcessStatus.PENDING)
        .setThirdInputParam("")
        .setThirdOutputParam("")
        .setInputParamData(new InputParam().setMobiles(Set.of(mobile))
            .setBizKey(SmsBizKey.SIGN_CANCEL)
            .setExpire(SmsConstants.DEFAULT_VC_VALID_SECOND)
            .setTemplateParams(Map.of("action", SmsBizKey.SIGN_CANCEL.getValue())))
        .setSendTenantId(PrincipalContext.getTenantId())
        .setSendUserId(PrincipalContext.getUserId())
        .setReceiveObjectType(ReceiveObjectType.USER)
        .setReceiveObjectIds(null)
        .setReceivePolicyCodes(null);
  }

}
