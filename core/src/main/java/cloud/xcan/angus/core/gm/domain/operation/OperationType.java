package cloud.xcan.angus.core.gm.domain.operation;


import cloud.xcan.angus.spec.ValueObject;

public enum OperationType implements ValueObject<OperationType> {
  CREATED, UPDATED, ENABLED, DISABLED, LOCKED, UNLOCKED, DELETED, SYNC, CUSTOM,
  SIGN_UP, SIGN_IN_SUCCESS, SIGN_IN_FAIL, SIGN_OUT, UPDATE_PASSWORD,
  SET_SYS_ADMIN, CANCEL_SYS_ADMIN,
  TARGET_TAG_UPDATED, TARGET_TAG_DELETED,
  APP_OPEN, APP_OPEN_RENEW, APP_OPEN_CANCEL,
  ;

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
