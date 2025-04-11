package cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org;

import cloud.xcan.angus.api.commonlink.policy.PolicyGrantStage;
import cloud.xcan.angus.api.commonlink.policy.PolicyType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class AppAuthPolicyVo {

  private Long id;

  private String name;

  private String code;

  private PolicyType type;

  private Boolean default0;

  private PolicyGrantStage grantStage;

  private Boolean enabled;

}
