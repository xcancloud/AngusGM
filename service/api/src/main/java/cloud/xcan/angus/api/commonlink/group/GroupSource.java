package cloud.xcan.angus.api.commonlink.group;


import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import lombok.Getter;

@EndpointRegister
@Getter
public enum GroupSource implements EnumMessage<String> {
  BACKGROUND_ADDED,
  LDAP_SYNCHRONIZE;

  @Override
  public String getValue() {
    return this.name();
  }
}
