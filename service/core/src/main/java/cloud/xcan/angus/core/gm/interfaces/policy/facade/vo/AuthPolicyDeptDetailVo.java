package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo;

import cloud.xcan.angus.remote.NameJoinField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class AuthPolicyDeptDetailVo {

  private Long id;

  private Long policyId;

  private Long deptId;

  private Long appId;

  @NameJoinField(id = "appId", repository = "appRepo")
  private String appName;

  @NameJoinField(id = "deptId", repository = "commonDeptRepo")
  private String deptName;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

}
