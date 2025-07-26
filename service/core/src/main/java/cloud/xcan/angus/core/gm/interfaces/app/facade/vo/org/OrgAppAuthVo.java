package cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org;

import cloud.xcan.angus.api.enums.AppType;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.enums.OpenStage;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.tag.AppTagInfoVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyOrgVo;
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

  private String name;

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
