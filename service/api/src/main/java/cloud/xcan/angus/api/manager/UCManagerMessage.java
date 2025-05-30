package cloud.xcan.angus.api.manager;

public interface UCManagerMessage {

  String TENANT_NOT_EXISTED_T = "xcm.gm.tenant.not.existed.t";

  String USER_NOT_EXISTED_T = "xcm.gm.user.not.existed.t";

  // **************User BUC001~BUC200************
  String USER_MOBILE_NOT_BIND_CODE = "BUC071";
  String USER_MOBILE_NOT_BIND = "xcm.gm.user.mobile.not.bind";
  String USER_EMAIL_NOT_BIND_CODE = "BUC071";
  String USER_EMAIL_NOT_BIND = "xcm.gm.user.email.not.bind";

  // **************Group BUC301~BUC350************
  String GROUP_IS_DISABLED_CODE = "BUC301";
  String GROUP_IS_DISABLED_T = "xcm.gm.group.is.disabled.t";

}
