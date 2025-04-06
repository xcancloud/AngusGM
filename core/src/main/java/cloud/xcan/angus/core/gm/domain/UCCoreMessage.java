package cloud.xcan.angus.core.gm.domain;

public interface UCCoreMessage {

  String TENANT_AUDIT_FAILURE_REASON_MISSING = "xcm.uc.tenant.audit.failure.reason.missing";
  String TENANT_CERT_SUMMITED = "xcm.uc.tenant.cert.summited";
  String TENANT_CERT_MISSING_T = "xcm.uc.tenant.cert.missing.t";
  String TENANT_REAL_NAME_PASSED = "xcm.uc.tenant.real.name.passed";
  String TENANT_IS_CANCELED = "xcm.uc.tenant.is.canceled";

  String TENANT_NAME_ID_CARD_INCONSISTENT = "xcm.uc.tenant.name.id.card.inconsistent";
  String TENANT_NAME_BUSINESS_INCONSISTENT = "xcm.uc.tenant.name.business.inconsistent";
  String TENANT_LEGAL_PERSON_BUSINESS_INCONSISTENT = "xcm.uc.tenant.legal.person.business.inconsistent";

  String USER_ACCOUNT_EXISTED_ERROR = "xcm.uc.user.account.existed.error.t";
  String USER_MAIN_DEPT_NUM_ERROR = "xcm.uc.user.main.dept.num.error.t";
  String USER_REFUSE_OPERATE_ADMIN = "xcm.uc.user.refuse.operate.admin.account";
  String USER_SYS_ADMIN_NUM_ERROR = "xcm.uc.user.sys.admin.num.error.t";

  String SEND_MOBILE_ERROR_T = "xcm.uc.send.mobile.error.t";
  String SEND_EMAIL_ERROR_T = "xcm.uc.send.email.error.t";
  String MOBILE_IS_UNBIND = "xcm.uc.mobile.unbind";
  String MOBILE_BIND_EXISTED = "xcm.uc.mobile.bind.existed";
  String EMAIL_BIND_EXISTED = "xcm.uc.email.bind.existed";

  String LINK_SECRET_TIMEOUT_ERROR = "xcm.uc.link.secret.timeout.error";
  String LINK_SECRET_ILLEGAL_ERROR = "xcm.uc.link.secret.illegal.error.t";

  String NOTICE_REAL_NAME_AUTH = "xcm.uc.serviceType.realNameAuthentication";
  String NOTICE_REAL_NAME_AUTH_FAILURE_REASON = "xcm.uc.serviceType.realNameAuthentication.failureReason";

}
