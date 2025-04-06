package cloud.xcan.angus.api.commonlink;

public interface EmailConstant {

  int MAX_MAIL_SERVER_QUOTA = 10;

  int MAX_SEND_RETRY_NUM = 3;

  int MAX_EMAIL_CONTEXT_SIZE = 200000;

  int MAX_EMAIL_ADDRESS = 500;
  int MAX_SEND_OBJECT_SIZE = MAX_EMAIL_ADDRESS;

  int MAX_ATTACHMENT_NUM = 10;

  int DEFAULT_VC_VALID_SECOND = 5 * 60;
  int MAX_VC_VALID_SECOND = 24 * 60 * 60;

  int VC_REPEAT_SEND_LIMIT_SECOND = 1 * 60;

  int MAX_SUBJECT_PREFIX_LENGTH = 80;
  int MAX_SUBJECT_LENGTH = 200;

  String VC_PARAM_NAME = "verificationCode";
  String VC_VARIABLE = "verificationCode";
  String VC_CACHE_PREFIX = "email:verificationCode";
  String VC_CACHE_REPEAT_CHECK_PREFIX = "email:verificationCode:repeat:check";

  String MAIL_CHANNEL_VARIABLE_PREFIX = "channelType";

}
