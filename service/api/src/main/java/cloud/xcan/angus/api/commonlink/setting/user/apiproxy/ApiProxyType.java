package cloud.xcan.angus.api.commonlink.setting.user.apiproxy;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import lombok.Getter;


@Getter
@EndpointRegister
public enum ApiProxyType implements EnumMessage<String> {
  NO_PROXY,
  CLIENT_PROXY,
  SERVER_PROXY,
  CLOUD_PROXY;

  @Override
  public String getValue() {
    return this.name();
  }
}
