package cloud.xcan.angus.core.gm.domain;

public interface EmailMessage {

  String NO_SEVER_AVAILABLE_T = "xcm.angusgm.no.sever.available.t";
  String NO_SEVER_AVAILABLE = "xcm.angusgm.no.sever.available";

  String SERVER_NAME_EXISTED_EXISTED_T = "xcm.angusgm.server.name.existed.t";

  String SERVER_OVER_LIMIT_CODE = "BEM103";
  String SERVER_OVER_LIMIT_T = "xcm.angusgm.server.over.limit.t";

  String TEMPLATE_IO_EXCEPTION_CODE = "BEM202";
  String TEMPLATE_IO_EXCEPTION = "xcm.angusgm.template.io.exception.t";
  String TEMPLATE_PARSE_EXCEPTION_CODE = "BEM203";
  String TEMPLATE_PARSE_EXCEPTION = "xcm.angusgm.template.parse.exception.t";

  String VERIFY_CODE_EXPIRED = "xcm.angusgm.email.verifyCode.expired";
  String VERIFY_CODE_ERROR = "xcm.angusgm.email.verifyCode.error";

  String TOO_MANY_ATTACHMENT = "xcm.angusgm.too.many.attachment.t";

  String SEND_EXCEPTION_CODE = "BEM305";
  String SEND_EXCEPTION = "xcm.angusgm.send.exception";

  String EMAIL_RECEIVER_IS_MISSING = "xcm.angusgm.email.receiver.is.missing";
  String VERIFY_CODE_TO_ADDRESS_IS_MISSING = "xcm.angusgm.email.verifyCode.toAddress.is.missing";

  String TEMPLATE_BIZ_KEY_NOT_NULL = "xcm.angusgm.template.bizKey.not.null";

  String NO_TEMPLATE_BIZ_CONFIG_CODE = "BEM4001";
  String NO_TEMPLATE_BIZ_CONFIG_T = "xcm.angusgm.no.template.biz.config.t";

}
