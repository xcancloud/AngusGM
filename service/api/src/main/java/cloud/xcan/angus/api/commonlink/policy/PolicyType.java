package cloud.xcan.angus.api.commonlink.policy;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import lombok.Getter;

/**
 * Authorization supports three policies:
 *
 * <pre>
 * OPEN_GRANT: Open the application authorization policy, which is invisible to non operating users;
 *
 * PRE_DEFINED: A predefined common authorization template policy for all tenants, visible to all tenants;
 *
 * USER_DEFINED: The tenant user defines the authorization policy, which is only visible to the tenant.
 * </pre>
 *
 * @author XiaoLong Liu
 */
@Getter
@EndpointRegister
public enum PolicyType implements EnumMessage<String> {

  //  OPEN_GRANT,
  PRE_DEFINED,
  USER_DEFINED;

  @Override
  public String getValue() {
    return this.name();
  }
}
