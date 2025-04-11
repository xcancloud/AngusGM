package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.api.commonlink.SmsConstants.VC_CACHE_PREFIX;
import static cloud.xcan.angus.api.commonlink.SmsConstants.VC_CACHE_REPEAT_CHECK_PREFIX;

import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.core.gm.domain.sms.channel.SmsChannel;
import cloud.xcan.angus.core.gm.domain.sms.template.SmsTemplate;

public class SmsConverter {

  public static SmsTemplate toSmsTemplate(Long id, SmsChannel channel, SmsTemplate initTemplate) {
    return new SmsTemplate().setId(id)
        .setCode(initTemplate.getCode())
        .setName(initTemplate.getName())
        .setChannelId(channel.getId())
        .setSignature(initTemplate.getSignature())
        .setContent(initTemplate.getContent())
        .setThirdCode(initTemplate.getThirdCode())
        .setLanguage(initTemplate.getLanguage())
        .setVerificationCode(initTemplate.getVerificationCode())
        .setVerificationCodeValidSecond(initTemplate.getVerificationCodeValidSecond());
  }

  public static String getVerificationCodeCacheKey(SmsBizKey bizKey, String mobile) {
    return VC_CACHE_PREFIX + ":" + bizKey.getValue() + ":" + mobile;
  }

  public static String getVerificationCodeRepeatCheckKey(SmsBizKey bizKey, String mobile) {
    return VC_CACHE_REPEAT_CHECK_PREFIX + ":" + bizKey.getValue() + ":" + mobile;
  }

}
