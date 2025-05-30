package cloud.xcan.angus.api.commonlink;

import static cloud.xcan.angus.spec.experimental.BizConstant.XCAN_2P_PLATFORM_CODE;
import static cloud.xcan.angus.spec.experimental.BizConstant.XCAN_TENANT_PLATFORM_CODE;

import java.util.List;

public interface AuthConstant {

  String POLICY_PRE_DEFINED_ADMIN_SUFFIX = "_ADMIN";
  String POLICY_PRE_DEFINED_USER_SUFFIX = "_USER";
  String POLICY_PRE_DEFINED_GUEST_SUFFIX = "_GUEST";
  String POLICY_PRE_DEFINED_EXT_SUFFIX = "_EXT";

  List<String> POLICY_PRE_DEFINED_SUFFIX = List.of(
      POLICY_PRE_DEFINED_ADMIN_SUFFIX,
      POLICY_PRE_DEFINED_USER_SUFFIX,
      POLICY_PRE_DEFINED_GUEST_SUFFIX,
      POLICY_PRE_DEFINED_EXT_SUFFIX
  );

  List<String> TOKEN_AUTH_RESOURCE_IGNORE_SUFFIX = List.of("Inner", "Pub");

  //////////////////// Policy ////////////////////////////

  /**
   * OP Policy Code
   **/
  String OP_PERMISSION_ADMIN = "OP_PERMISSION_ADMIN";
  String OP_PERMISSION_USER = "OP_PERMISSION_USER";
  String OP_PLATFORM_ADMIN = "OP_PLATFORM_ADMIN";

  /**
   * TO Policy Code
   */
  String TOP_PERMISSION_ADMIN = "TOP_PERMISSION_ADMIN";
  //////////////////// Policy ////////////////////////////

  int MAX_SYS_TOKEN_NUM = 3;
  int DEFAULT_TOKEN_EXPIRE_SECOND = 30 * 24 * 60 * 60;

  int APP_MAX_TAG = 10;

  int APP_OPEN_YEAR = 50;

  int MAX_APP_FUNC_SHORT_NAME_LENGTH = 40;
  int MAX_APP_FUNC_API_NUM_AP = 50;

  int MAX_POLICY_FUNC_NUM = 2000;

  String USER_INFO_ENDPOINT = "/api/v1/auth/user";
  String TOKEN_REVOKE_ENDPOINT = "/api/v1/auth/token";

  String SWAGGER_API_URL = "/v3/api-docs/user";
  String SWAGGER_PUB_API_URL = "/v3/api-docs/public";
  String SWAGGER_INNER_API_URL = "/v3/api-docs/inner";
  String SWAGGER_OPEN_API_TO_PRIVATE_URL = "/v3/api-docs/openapi2p";

  // Used in infra-security
  int DEFAULT_TOKEN_SALT_LENGTH = 32;

  // Default 12 hours.
  int REFRESH_TOKEN_VALIDITY_SECONDS = 60 * 60 * 12;
  // Default 1 hours.
  int ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 60;

  boolean SUPPORT_REFRESH_TOKEN = false;

  //int LINK_SECRET_LENGTH = 40;
  int LINK_SECRET_VALID_SECOND = 5 * 60;

  String CACHE_EMAIL_CHECK_SECRET_PREFIX = "gm:aas:checkEmail:%s:%s";
  String CACHE_SMS_CHECK_SECRET_PREFIX = "gm:aas:checkSms:%s:%s";
  String CACHE_USER_SOCIAL_CHECK_SECRET_PREFIX = "gm:aas:user:checkSocial:%d";
  String CACHE_SOCIAL_CHECK_SECRET_PREFIX = "gm:aas:checkSocial:%s";
  String CACHE_PASSWORD_ERROR_NUM_PREFIX = "gm:aas:user:passwordErrorNum:%s";
  String CACHE_PASSWORD_ERROR_LOCKED_PREFIX = "gm:aas:user:passwordErrorLocked:%s";

  /**
   * For system token
   */
  String SYS_TOKEN_CLIENT_ID_FMT = XCAN_TENANT_PLATFORM_CODE + "_t%s_s%s";
  String SYS_TOKEN_CLIENT_DESC_FMT = "Tenant [%s] system token `%s` oauth2 client";

  /**
   * For private application open access
   *
   * @see `FeignOpen2pAuthInterceptor`
   */
  String SIGN2P_CLIENT_ID_FMT = XCAN_2P_PLATFORM_CODE + "_t%s_b%s_r%s";
  String SIGN2P_CLIENT_NAME_FMT = "Tenant[%s]-[%s][%s]";

}
