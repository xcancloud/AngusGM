package cloud.xcan.angus.core.gm.domain.operation;


import cloud.xcan.angus.spec.ValueObject;

public enum OperationType implements ValueObject<OperationType> {
  CREATED, UPDATED, ENABLED, DISABLED, DELETE, CUSTOM,
  SIGN_UP, SIGN_IN_SUCCESS, SIGN_IN_FAIL, SIGN_OUT, UPDATE_PASSWORD;

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
