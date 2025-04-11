package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.org;

import cloud.xcan.angus.api.commonlink.policy.PolicyType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AuthDefaultPolicyVo {

  private Long id;

  private String code;

  private String name;

  private PolicyType type;

  private Boolean currentDefault;

  private String description;

}
