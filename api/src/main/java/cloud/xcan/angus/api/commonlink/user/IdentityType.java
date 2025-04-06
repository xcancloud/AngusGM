package cloud.xcan.angus.api.commonlink.user;

import cloud.xcan.angus.spec.experimental.Value;

public enum IdentityType implements Value<String> {

  ID, MOBILE, EMAIL, USERNAME;

  @Override
  public String getValue() {
    return this.name();
  }

}
