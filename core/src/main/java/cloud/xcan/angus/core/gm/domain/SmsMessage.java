package cloud.xcan.angus.core.gm.domain;


public interface SmsMessage {

  String SMS_CHANNEL_NOT_AVAILABLE_CODE = "BSM000";
  String SMS_CHANNEL_NOT_AVAILABLE = "xcm.angusgm.channel.not.available";

  String SMS_NO_PLUGIN_ERROR_CODE = "BSM015";
  String SMS_NO_PLUGIN_ERROR = "xcm.angusgm.sms.no.plugin.error";
  String SMS_NO_PLUGIN_CHANNEL_ERROR = "xcm.angusgm.sms.plugin.channel.not.existed.t";

  String NO_TEMPLATE_BIZ_CONFIG_CODE = "BSM030";
  String NO_TEMPLATE_BIZ_CONFIG_T = "xcm.angusgm.template.no.biz.config.t";

  String VERIFY_CODE_EXPIRED = "xcm.angusgm.sms.verifyCode.expired";
  String VERIFY_CODE_ERROR = "xcm.angusgm.sms.verifyCode.error";

  String VERIFY_CODE_SEND_REPEATED_CODE = "BSM042";
  String VERIFY_CODE_SEND_REPEATED = "xcm.angusgm.sms.verifyCode.send.repeated";

  String VERIFY_CODE_MOBILES_IS_MISSING = "xcm.angusgm.sms.verifyCode.mobiles.is.missing";

  String SMS_RECEIVER_IS_MISSING = "xcm.angusgm.sms.receiver.is.missing";

  String SMS_SEND_ERROR_CODE = "BSM043";
  String SMS_SEND_ERROR = "xcm.angusgm.sms.send.error";

}
