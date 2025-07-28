package cloud.xcan.angus.core.gm.interfaces.app.facade.vo.func;

import cloud.xcan.angus.api.commonlink.app.func.AppFuncType;
import cloud.xcan.angus.api.gm.app.vo.AppTagInfoVo;
import cloud.xcan.angus.remote.NameJoinField;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AppFuncVo {

  private Long id;
  private String name;
  private String code;

  private AppFuncType type;
  private String showName;
  private Long pid;
  private String icon;
  private String url;
  private Long appId;
  private Integer sequence;

  /**
   * Is it authorized：true:Authorized false:unauthorized
   */
  @Schema(description = "Whether to authorize control")
  private Boolean authCtrl;

  private Boolean enabled;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  private List<AppTagInfoVo> tags;

}
