package cloud.xcan.angus.api.commonlink.tenant;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import lombok.Getter;

@Getter
@EndpointRegister
public enum TenantRealNameStatus implements EnumMessage<String> {
  NOT_SUBMITTED,
  AUDITING,
  AUDITED,
  FAILED_AUDIT;

  @Override
  public String getValue() {
    return this.name();
  }
}
