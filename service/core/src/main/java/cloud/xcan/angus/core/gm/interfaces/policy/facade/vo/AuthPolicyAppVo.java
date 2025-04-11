package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class AuthPolicyAppVo {

  private Long appId;

  private String appName;

  private List<AuthPolicyDefaultVo> polices;

}
