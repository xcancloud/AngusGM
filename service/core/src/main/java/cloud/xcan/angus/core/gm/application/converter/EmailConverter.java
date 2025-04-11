package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.api.commonlink.EmailConstant.VC_CACHE_PREFIX;
import static cloud.xcan.angus.api.commonlink.EmailConstant.VC_CACHE_REPEAT_CHECK_PREFIX;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.template.EmailTemplate;

public class EmailConverter {

  public static long getVerificationCodeValidSecond(Email email, EmailTemplate emailTemplate) {
    return nonNull(email.getVerificationCodeValidSecond()) &&
        email.getVerificationCodeValidSecond() > 0 ? email.getVerificationCodeValidSecond()
        : emailTemplate.getVerificationCodeValidSecond();
  }

  public static String getVerificationCodeCacheKey(EmailBizKey bizKey, String email) {
    return VC_CACHE_PREFIX + ":" + bizKey.getValue() + ":" + email;
  }

  public static String getVerificationCodeRepeatCheckKey(EmailBizKey bizKey, String email) {
    return VC_CACHE_REPEAT_CHECK_PREFIX + ":" + bizKey.getValue() + ":" + email;
  }

}
