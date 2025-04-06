package cloud.xcan.angus.core.gm.domain;


public interface AASCoreMessage {

  String API_IS_DISABLED_T = "xcm.aas.api.is.disabled.t";
  String API_SWAGGER_PARSE_ERROR_CODE = "BAA002";
  String API_SWAGGER_PARSE_ERROR = "xcm.aas.api.swagger.parse.error.t";

  String SERVICE_IS_DISABLED_T = "xcm.aas.service.is.disabled.t";
  String SERVICE_DISCOVERY_NOT_EXISTED_T = "xcm.aas.service.discovery.not.existed.t";

  String APP_IS_DISABLED_T = "xcm.aas.application.is.disabled.t";

  String APP_TAG_EXIST_ERROR_T = "xcm.aas.app.tag.existed.t";
  String APP_TAG_REPEAT_ERROR_T = "xcm.aas.app.tag.repeated.t";

  String APP_FUNC_IS_DISABLED_T = "xcm.aas.func.is.disabled.t";
  String APP_FUNC_CODE_REPEATED_T = "xcm.aas.func.code.repeated.t";
  String APP_FUNC_CODE_EXISTED_T = "xcm.aas.func.code.existed.t";

  String APP_OPEN_NOT_FOUND_T = "xcm.aas.app.open.not.found.t";
  String APP_OPEN_EXPIRED_T = "xcm.aas.app.open.expired.t";

  String POLICY_IS_DISABLED_T = "xcm.aas.policy.is.disabled.t";
  String POLICY_PRE_SUFFIX_ERROR_T = "xcm.aas.policy.pre.defined.suffix.error.t";

  String TO_POLICY_IS_DISABLED_T = "xcm.aas.to.policy.is.disabled.t";

  String CLIENT_IS_DISABLED_T = "xcm.aas.client.is.disabled.t";

  String TOKEN_NAME_EXISTED_T = "xcm.aas.token.name.existed.t";
  String TOKEN_NOT_SING_IN_LOGOUT_CODE = "BAA351";
  String TOKEN_NOT_SING_IN_LOGOUT = "xcm.aas.token.not.signin.logout.t";

  String PASSWORD_CANNOT_SAME = "xcm.aas.password.cannot.same";
  String PASSWORD_IS_TOO_SHORT_T = "xcm.aas.password.is.too.short.t";
  String OLD_PASSWORD_ERROR = "xcm.aas.old.password.error";
  String SIGN_IN_ACCOUNT_EMPTY = "xcm.aas.sign.in.account.empty";
  String SIGN_IN_DEVICE_ID_EMPTY = "xcm.aas.sign.in.deviceId.empty";
  String SIGN_IN_PASSWORD_ERROR = "xcm.aas.sign.in.password.error";
  String SIGN_IN_NO_TO_USER_ERROR = "xcm.aas.sign.in.no.touser.error";
  String SIGN_IN_PASSWORD_ERROR_OVER_LIMIT_CODE = "BAA405";
  String SIGN_IN_PASSWORD_ERROR_OVER_LIMIT_T = "xcm.aas.sign.in.password.error.over.limit.t";
  String SIGN_IN_PASSWORD_ERROR_LOCKED_RETRY_CODE = "BAA406";
  String SIGN_IN_PASSWORD_ERROR_LOCKED_RETRY_T = "xcm.aas.sign.in.password.error.locked.retry.t";
  String LINK_SECRET_TIMEOUT = "xcm.aas.linkSecret.timeout";
  String LINK_SECRET_ILLEGAL = "xcm.aas.linkSecret.illegal";
  String PASSWORD_HAS_BEEN_INITIALIZED = "xcm.aas.password.has.been.initialized";

}
