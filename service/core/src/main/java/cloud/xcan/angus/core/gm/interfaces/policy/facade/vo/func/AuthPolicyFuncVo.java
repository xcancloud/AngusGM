package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.func;

import cloud.xcan.angus.api.commonlink.app.func.AppFuncType;
import cloud.xcan.angus.api.gm.app.vo.ApiInfoVo;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class AuthPolicyFuncVo {

  private Long id;

  private String code;

  private String name;

  private String showName;

  private Long pid;

  private String icon;

  private AppFuncType type;

  private Boolean authCtrl;

  private Boolean enabled;

  private String url;

  private Integer sequence;

  private List<ApiInfoVo> apis;

}
