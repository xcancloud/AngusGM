package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.org;

import cloud.xcan.angus.api.commonlink.app.AppType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AuthAppDefaultPolicyVo {

  private Long appId;

  private String appCode;

  private AppType appType;

  private String appName;

  private String version;

  private List<AuthDefaultPolicyVo> defaultPolicies;

}
