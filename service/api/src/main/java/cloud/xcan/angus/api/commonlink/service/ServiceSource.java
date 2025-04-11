package cloud.xcan.angus.api.commonlink.service;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import lombok.Getter;


@Getter
@EndpointRegister
public enum ServiceSource implements EnumMessage<String> {
  BACK_ADD,
  EUREKA/*,
  NACOS,
  CONSUL*/;

  @Override
  public String getValue() {
    return this.name();
  }
}
