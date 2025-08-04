package cloud.xcan.angus.core.gm.interfaces.user.facade.vo.func;

import cloud.xcan.angus.api.enums.AppType;
import cloud.xcan.angus.api.gm.app.vo.ApiInfoVo;
import cloud.xcan.angus.remote.MessageJoinField;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AuthAppVo {

  private Long id;

  private String code;

  private String version;

  @MessageJoinField(type = "APP")
  private String name;

  @MessageJoinField(type = "APP")
  private String showName;

  private String icon;

  private AppType type;

  private Boolean authCtrl;

  private String url;

  private Integer sequence;

  private List<ApiInfoVo> apis;

  private List<AuthAppFuncVo> appFuncs;
}
