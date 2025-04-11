package cloud.xcan.angus.api.commonlink.user;

import cloud.xcan.angus.spec.ValueObject;
import lombok.Getter;

@Getter
public enum SignupType implements ValueObject<SignupType> {

  MOBILE("mobile"),
  EMAIL("email"),
  NOOP("NOOP");

  private final String value;

  SignupType(String value) {
    this.value = value;
  }

  public boolean isMobile() {
    return MOBILE.equals(this);
  }

  public boolean isEmail() {
    return EMAIL.equals(this);
  }

}
