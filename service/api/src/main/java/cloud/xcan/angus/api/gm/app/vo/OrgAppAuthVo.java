package cloud.xcan.angus.api.gm.app.vo;

import cloud.xcan.angus.api.enums.AppType;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.enums.OpenStage;
import cloud.xcan.angus.api.gm.policy.vo.AuthPolicyOrgVo;
import cloud.xcan.angus.remote.MessageJoinField;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class OrgAppAuthVo {

  private Long id;

  private EditionType editionType;

  private String code;

  @MessageJoinField(type = "APP")
  private String name;

  @MessageJoinField(type = "APP")
  private String showName;

  private String icon;

  private AppType type;

  private Boolean authCtrl;

  private Boolean enabled;

  private String url;

  private Integer sequence;

  private String version;

  private OpenStage openStage;

  private List<AuthPolicyOrgVo> policies;

  private List<AppTagInfoVo> tags;

}
