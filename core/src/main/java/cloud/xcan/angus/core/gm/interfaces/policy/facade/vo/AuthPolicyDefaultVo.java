package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo;

import cloud.xcan.angus.api.commonlink.policy.PolicyType;
import cloud.xcan.angus.remote.NameJoinField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class AuthPolicyDefaultVo {

  private Long id;

  private Long policyId;

  @NameJoinField(id = "policyId", repository = "policyRepo")
  private String name;

  private String code;

  private PolicyType type;

  private Boolean set;

}
