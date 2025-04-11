package cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org;

import cloud.xcan.angus.api.commonlink.group.GroupSource;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyOrgVo;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class AppAuthGroupVo {

  private Long id;

  private String name;

  private String code;

  private Boolean enabled;

  private GroupSource source;

  private List<AuthPolicyOrgVo> policies;
}
