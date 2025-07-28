package cloud.xcan.angus.api.gm.app.vo;

import cloud.xcan.angus.api.commonlink.app.func.AppFuncType;
import cloud.xcan.angus.spec.utils.TreeUtils.TreeNode;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class AuthAppFuncTreeVo implements TreeNode<AuthAppFuncTreeVo> {

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

  private List<AuthAppFuncTreeVo> children;

  @Override
  public void setChildren(List<AuthAppFuncTreeVo> children) {
    this.children = children;
  }

  @Override
  public Long getPid() {
    return pid;
  }

  @Override
  public Long getSequence0() {
    return Long.valueOf(sequence);
  }

}
