package cloud.xcan.angus.core.gm.interfaces.user.facade.vo.func;

import cloud.xcan.angus.api.commonlink.app.func.AppFuncType;
import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiInfoVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.tag.AppTagInfoVo;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class AuthAppFuncVo {

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

  private Boolean hasAuth;

  private List<ApiInfoVo> apis;

  private List<AppTagInfoVo> tags;

}
