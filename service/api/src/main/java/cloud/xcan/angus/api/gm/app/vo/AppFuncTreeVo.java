package cloud.xcan.angus.api.gm.app.vo;

import cloud.xcan.angus.api.commonlink.app.func.AppFuncType;
import cloud.xcan.angus.spec.utils.TreeUtils.TreeNode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AppFuncTreeVo implements TreeNode<AppFuncTreeVo> {

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

  @Schema(description = "Whether to authorize control")
  private Boolean authCtrl;

  private Boolean enabled;

  private Long createdBy;
  private String creator;

  //private String tenantName;

  //private List<ApiInfoVo> apis;

  private List<AppTagInfoVo> tags;

  private List<AppFuncTreeVo> children;

  private Boolean hit;

  @Override
  public void setChildren(List<AppFuncTreeVo> children) {
    this.children = children;
  }

  @JsonIgnore
  @Override
  public Long getSequence0() {
    return Long.valueOf(sequence);
  }
}
