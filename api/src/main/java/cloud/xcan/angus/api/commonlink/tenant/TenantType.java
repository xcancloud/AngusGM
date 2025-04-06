package cloud.xcan.angus.api.commonlink.tenant;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import lombok.Getter;

@Getter
@EndpointRegister
public enum TenantType implements EnumMessage<String> {
  PERSONAL,
  ENTERPRISE,
  GOVERNMENT,
  UNKNOWN;

  @Override
  public String getValue() {
    return this.name();
  }

  public boolean isPersonal() {
    return this.equals(PERSONAL);
  }

  public boolean isEnterprise() {
    return this.equals(ENTERPRISE);
  }
}
