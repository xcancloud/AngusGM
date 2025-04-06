package cloud.xcan.angus.api.commonlink.policy;

import cloud.xcan.angus.spec.ValueObject;
import cloud.xcan.angus.spec.experimental.EndpointRegister;
import lombok.Getter;


@EndpointRegister
@Getter
public enum PolicyGrantScope implements ValueObject<PolicyGrantScope> {

  TENANT_SYS_ADMIN,
  TENANT_ALL_USER,
  TENANT_ORG;

}
