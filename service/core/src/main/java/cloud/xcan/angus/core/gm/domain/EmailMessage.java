package cloud.xcan.angus.core.gm.domain;

public interface EmailMessage {

  String NO_SEVER_AVAILABLE_T = "xcm.gm.no.sever.available.t";
  String NO_SEVER_AVAILABLE = "xcm.gm.no.sever.available";

  String SERVER_NAME_EXISTED_EXISTED_T = "xcm.gm.server.name.existed.t";

  String SERVER_OVER_LIMIT_CODE = "BEM103";
  String SERVER_OVER_LIMIT_T = "xcm.gm.server.over.limit.t";

  String TEMPLATE_IO_EXCEPTION_CODE = "BEM202";
  String TEMPLATE_IO_EXCEPTION = "xcm.gm.template.io.exception.t";
  String TEMPLATE_PARSE_EXCEPTION_CODE = "BEM203";
  String TEMPLATE_PARSE_EXCEPTION = "xcm.gm.template.parse.exception.t";

  String VERIFY_CODE_EXPIRED = "xcm.gm.email.verifyCode.expired";
  String VERIFY_CODE_ERROR = "xcm.gm.email.verifyCode.error";

  String TOO_MANY_ATTACHMENT = "xcm.gm.too.many.attachment.t";

  String SEND_EXCEPTION_CODE = "BEM305";
  String SEND_EXCEPTION = "xcm.gm.send.exception";

  String EMAIL_RECEIVER_IS_MISSING = "xcm.gm.email.receiver.is.missing";
  String VERIFY_CODE_TO_ADDRESS_IS_MISSING = "xcm.gm.email.verifyCode.toAddress.is.missing";

  String TEMPLATE_BIZ_KEY_NOT_NULL = "xcm.gm.template.bizKey.not.null";

  String NO_TEMPLATE_BIZ_CONFIG_CODE = "BEM4001";
  String NO_TEMPLATE_BIZ_CONFIG_T = "xcm.gm.no.template.biz.config.t";

}
