package cloud.xcan.angus.api.commonlink.app;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import lombok.Getter;


@EndpointRegister
@Getter
public enum AppType implements EnumMessage<String> {

  /**
   * Cloud Applications: Deliver scalable, on-demand services via cloud infrastructure, enabling
   * remote access and elastic resource allocation.
   */
  CLOUD_APP,
  /**
   * Core Base Applications: Provide essential system functionalities and foundational services
   * (e.g., authentication, data storage, system management) critical for platform operations.
   */
  BASE_APP,
  /**
   * Operational Applications: The operations platform centralizes and orchestrates business
   * processes, real-time analytics, and system monitoring to enhance operational efficiency.
   */
  OP_APP;

  @Override
  public String getValue() {
    return this.name();
  }
}
