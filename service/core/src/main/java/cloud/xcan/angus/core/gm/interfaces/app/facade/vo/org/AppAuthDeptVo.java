package cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org;

import cloud.xcan.angus.api.gm.policy.vo.AuthPolicyOrgVo;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class AppAuthDeptVo {

  private Long id;

  private String code;

  private String name;

  private Long pid;

  private Integer level;

  private List<AuthPolicyOrgVo> policies;

}
