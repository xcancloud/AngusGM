package cloud.xcan.angus.core.gm.interfaces.user.facade.vo.func;

import cloud.xcan.angus.api.commonlink.app.func.AppFuncType;
import cloud.xcan.angus.api.gm.app.vo.ApiInfoVo;
import cloud.xcan.angus.api.gm.app.vo.AppTagInfoVo;
import cloud.xcan.angus.remote.MessageJoinField;
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

  @MessageJoinField(type = "MENU")
  private String name;

  @MessageJoinField(type = "MENU")
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
