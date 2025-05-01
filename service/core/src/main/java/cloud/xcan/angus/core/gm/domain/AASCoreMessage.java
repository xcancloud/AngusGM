package cloud.xcan.angus.core.gm.domain;


public interface AASCoreMessage {

  String API_IS_DISABLED_T = "xcm.gm.api.is.disabled.t";
  String API_SWAGGER_PARSE_ERROR_CODE = "BAA002";
  String API_SWAGGER_PARSE_ERROR = "xcm.gm.api.swagger.parse.error.t";

  String SERVICE_IS_DISABLED_T = "xcm.gm.service.is.disabled.t";
  String SERVICE_EUREKA_NOT_EXISTED_T = "xcm.gm.service.eureka.not.existed.t";

  String APP_IS_DISABLED_T = "xcm.gm.application.is.disabled.t";

  String APP_TAG_EXIST_ERROR_T = "xcm.gm.app.tag.existed.t";
  String APP_TAG_REPEAT_ERROR_T = "xcm.gm.app.tag.repeated.t";

  String APP_FUNC_IS_DISABLED_T = "xcm.gm.func.is.disabled.t";
  String APP_FUNC_CODE_REPEATED_T = "xcm.gm.func.code.repeated.t";
  String APP_FUNC_CODE_EXISTED_T = "xcm.gm.func.code.existed.t";

  String APP_OPEN_NOT_FOUND_T = "xcm.gm.app.open.not.found.t";
  String APP_OPEN_EXPIRED_T = "xcm.gm.app.open.expired.t";

  String POLICY_IS_DISABLED_T = "xcm.gm.policy.is.disabled.t";
  String POLICY_PRE_SUFFIX_ERROR_T = "xcm.gm.policy.pre.defined.suffix.error.t";

  String TO_POLICY_IS_DISABLED_T = "xcm.gm.to.policy.is.disabled.t";

  String CLIENT_IS_DISABLED_T = "xcm.gm.client.is.disabled.t";

  String TOKEN_NAME_EXISTED_T = "xcm.gm.token.name.existed.t";
  String TOKEN_NOT_SING_IN_LOGOUT_CODE = "BAA351";
  String TOKEN_NOT_SING_IN_LOGOUT = "xcm.gm.token.not.signIn.logout.t";

  String PASSWORD_CANNOT_SAME = "xcm.gm.password.cannot.same";
  String PASSWORD_IS_TOO_SHORT_T = "xcm.gm.password.is.too.short.t";
  String OLD_PASSWORD_ERROR = "xcm.gm.old.password.error";
  String SIGN_IN_ACCOUNT_EMPTY = "xcm.gm.sign.in.account.empty";
  String SIGN_IN_DEVICE_ID_EMPTY = "xcm.gm.sign.in.deviceId.empty";
  String SIGN_IN_PASSWORD_ERROR = "xcm.gm.sign.in.password.error";
  String SIGN_IN_NO_TO_USER_ERROR = "xcm.gm.sign.in.no.toUser.error";
  String SIGN_IN_PASSWORD_ERROR_OVER_LIMIT_CODE = "BAA405";
  String SIGN_IN_PASSWORD_ERROR_OVER_LIMIT_T = "xcm.gm.sign.in.password.error.over.limit.t";
  String SIGN_IN_PASSWORD_ERROR_LOCKED_RETRY_CODE = "BAA406";
  String SIGN_IN_PASSWORD_ERROR_LOCKED_RETRY_T = "xcm.gm.sign.in.password.error.locked.retry.t";
  String LINK_SECRET_TIMEOUT = "xcm.gm.linkSecret.timeout";
  String LINK_SECRET_ILLEGAL = "xcm.gm.linkSecret.illegal";
  String PASSWORD_HAS_BEEN_INITIALIZED = "xcm.gm.password.has.been.initialized";

}
