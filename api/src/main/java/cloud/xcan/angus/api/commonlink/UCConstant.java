package cloud.xcan.angus.api.commonlink;

public interface UCConstant {

  String BID_USERNAME = "user";
  String BID_TENANT = "tenant";

  //////////////////// Policy ////////////////////////////
  /**
   * GM Policy Code
   **/
  String GM_ADMIN = "GM_ADMIN";
  String GM_USER = "GM_USER";
  String GM_GUEST = "GM_GUEST";

  /**
   * OP Policy Code
   **/
  String OP_TENANT_ADMIN = "OP_TENANT_ADMIN";
  String OP_TENANT_USER = "OP_TENANT_USER";

  /**
   * (Top) Multi Tenant OP Role Code
   **/
  String TOP_TENANT_ADMIN = "TOP_TENANT_ADMIN";
  //////////////////// Policy ////////////////////////////

  /**
   * 1 Day
   */
  int TENANT_SIGN_CANCEL_LOCK_MS = 24 * 60 * 60;

  /**
   * User Directory Quota
   */
  int MAX_USER_DIRECTORY_NUM = 10;

  int DEFAULT_SECRET_VALID_SECOND = 5 * 60;
  String CACHE_EMAIL_CHECK_SECRET_PREFIX = "uc:checkEmail:%s:%d";
  String CACHE_SMS_CHECK_SECRET_PREFIX = "uc:checkSms:%s:%d";

  String LDAP_GROUP_CODE_PREFIX = "LDAP_";

  String PASSWORD_PROXY_ENCRYP_TYPE = "LDAP-PROXY";
  String PASSWORD_ENCRYP_TYPE_PREFIX = "{";
  String PASSWORD_ENCRYP_TYPE_SUFFIX = "}";
  String PASSWORD_PROXY_ENCRYP = PASSWORD_ENCRYP_TYPE_PREFIX + PASSWORD_PROXY_ENCRYP_TYPE
      + PASSWORD_ENCRYP_TYPE_SUFFIX;

}
