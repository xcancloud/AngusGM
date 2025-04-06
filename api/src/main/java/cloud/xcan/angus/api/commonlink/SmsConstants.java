package cloud.xcan.angus.api.commonlink;


public interface SmsConstants {

  /**
   * AliYun 1000, HuaweiYun 500
   */
  int MAX_MOBILE_SIZE = 500;
  int MAX_SEND_OBJECT_SIZE = MAX_MOBILE_SIZE;
  int MAX_SMS_TEMPLATE_PARAM = 50;

  int MAX_VC_VALID_MINUTE = 24 * 60;
  int DEFAULT_VC_VALID_SECOND = 5 * 60;

  String VC_PARAM_NAME = "verificationCode";
  String VC_TEMPLATE_VALID_MINUTE = "validMinute";
  String VC_CACHE_PREFIX = "sms:verificationCode";
  String VC_CACHE_REPEAT_CHECK_PREFIX = "sms:verificationCode:repeat:check";

}
