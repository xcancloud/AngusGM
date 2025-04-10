package cloud.xcan.angus.core.gm.domain.operation;


import cloud.xcan.angus.spec.ValueObject;

public enum OperationType implements ValueObject<OperationType> {
  // @formatter:off
  CREATED, UPDATED, ENABLED, DISABLED, LOCKED, UNLOCKED, DELETED, SYNC, CUSTOM,
  SIGN_UP, SIGN_IN_SUCCESS, SIGN_IN_FAIL, SIGN_OUT, UPDATE_PASSWORD,
  ADD_USER_DEPT, ADD_DEPT_USER, ADD_USER_GROUP, ADD_GROUP_USER,
  UPDATE_USER_DEPT, UPDATE_DEPT_USER, UPDATE_USER_GROUP, UPDATE_GROUP_USER,
  DELETE_USER_DEPT, DELETE_DEPT_USER, DELETE_USER_GROUP, DELETE_GROUP_USER,
  SET_USER_MAIN_DEPT, CANCEL_USER_MAIN_DEPT,
  SET_SYS_ADMIN, CANCEL_SYS_ADMIN, TENANT_CANCEL, SUBMIT_TENANT_AUDIT, TENANT_AUDIT,
  TARGET_TAG_UPDATED, TARGET_TAG_DELETED,
  APP_OPEN, APP_OPEN_RENEW, APP_OPEN_CANCEL;
  // @formatter:on

  public String getValue() {
    return this.name();
  }

  public String getDescMessageKey() {
    return "xcm.angusgm.activity." + this.name();
  }

  public String getDetailMessageKey() {
    return "xcm.angusgm.activity.detail." + this.name();
  }

}
