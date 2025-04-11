package cloud.xcan.angus.core.gm.domain.operation;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;

@EndpointRegister
public enum ModifiedResourceType implements EnumMessage<String> {
  SOCIAL,
  AI_AGENT,
  API_LOG,
  OPERATION_LOG,
  SYSTEM_LOG,
  MAX_RESOURCE_ACTIVITIES,
  MAX_METRICS_DAYS,
  TENANT_LOCALE,
  TENANT_SECURITY,
  TENANT_INVITATION_CODE;

  @Override
  public String getValue() {
    return "";
  }
}
