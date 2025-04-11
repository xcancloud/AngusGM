package cloud.xcan.angus.api.commonlink;

import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import lombok.Getter;

@Getter
@EndpointRegister
public enum AuthOrgType implements EnumMessage<String> {
  TENANT,
  USER,
  DEPT,
  GROUP;

  @Override
  public String getValue() {
    return this.name();
  }

  public OrgTargetType toOrgTargetType() {
    return OrgTargetType.valueOf(this.name());
  }
}
