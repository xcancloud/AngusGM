package cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org;

import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyOrgVo;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class AppAuthUserVo {

  private Long id;

  private String fullname;

  private String avatar;

  private Boolean globalAuth;

  private List<AuthPolicyOrgVo> policies;

}
