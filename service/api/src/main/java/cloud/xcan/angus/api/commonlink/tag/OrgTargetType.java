package cloud.xcan.angus.api.commonlink.tag;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import lombok.Getter;


@Getter
@EndpointRegister
public enum OrgTargetType implements EnumMessage<String> {
  /*TENANT*/
  USER,
  DEPT,
  GROUP;

  @Override
  public String getValue() {
    return this.name();
  }
}
