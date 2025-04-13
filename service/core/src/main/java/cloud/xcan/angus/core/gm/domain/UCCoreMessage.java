package cloud.xcan.angus.core.gm.domain;

public interface UCCoreMessage {

  String TENANT_AUDIT_FAILURE_REASON_MISSING = "xcm.gm.tenant.audit.failure.reason.missing";
  String TENANT_CERT_SUMMITED = "xcm.gm.tenant.cert.summited";
  String TENANT_CERT_MISSING_T = "xcm.gm.tenant.cert.missing.t";
  String TENANT_REAL_NAME_PASSED = "xcm.gm.tenant.real.name.passed";
  String TENANT_IS_CANCELED = "xcm.gm.tenant.is.canceled";

  String TENANT_NAME_ID_CARD_INCONSISTENT = "xcm.gm.tenant.name.id.card.inconsistent";
  String TENANT_NAME_BUSINESS_INCONSISTENT = "xcm.gm.tenant.name.business.inconsistent";
  String TENANT_LEGAL_PERSON_BUSINESS_INCONSISTENT = "xcm.gm.tenant.legal.person.business.inconsistent";

  String USER_ACCOUNT_EXISTED_ERROR = "xcm.gm.user.account.existed.error.t";
  String USER_MAIN_DEPT_NUM_ERROR = "xcm.gm.user.main.dept.num.error.t";
  String USER_REFUSE_OPERATE_ADMIN = "xcm.gm.user.refuse.operate.admin.account";
  String USER_SYS_ADMIN_NUM_ERROR = "xcm.gm.user.sys.admin.num.error.t";

  String SEND_MOBILE_ERROR_T = "xcm.gm.send.mobile.error.t";
  String SEND_EMAIL_ERROR_T = "xcm.gm.send.email.error.t";
  String MOBILE_IS_UNBIND = "xcm.gm.mobile.unbind";
  String MOBILE_BIND_EXISTED = "xcm.gm.mobile.bind.existed";
  String EMAIL_BIND_EXISTED = "xcm.gm.email.bind.existed";

  String LINK_SECRET_TIMEOUT_ERROR = "xcm.gm.link.secret.timeout.error";
  String LINK_SECRET_ILLEGAL_ERROR = "xcm.gm.link.secret.illegal.error.t";

  String NOTICE_REAL_NAME_AUTH = "xcm.gm.serviceType.realNameAuthentication";
  String NOTICE_REAL_NAME_AUTH_FAILURE_REASON = "xcm.gm.serviceType.realNameAuthentication.failureReason";

}
