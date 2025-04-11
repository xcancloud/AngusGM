package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo;

import cloud.xcan.angus.remote.NameJoinField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class AuthPolicyGroupDetailVo {

  private Long id;

  private Long policyId;

  private Long groupId;

  private Long appId;

  @NameJoinField(id = "appId", repository = "appRepo")
  private String appName;

  @NameJoinField(id = "groupId", repository = "commonGroupRepo")
  private String groupName;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

}
