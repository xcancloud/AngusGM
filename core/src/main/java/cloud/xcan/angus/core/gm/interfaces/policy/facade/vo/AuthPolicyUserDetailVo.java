package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo;

import cloud.xcan.angus.remote.NameJoinField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class AuthPolicyUserDetailVo {

  private Long id;

  private Long policyId;

  private Long appId;

  @NameJoinField(id = "appId", repository = "appRepo")
  private String appName;

  private Long userId;

  @NameJoinField(id = "userId", repository = "commonUserBaseRepo")
  private String userName;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  private String avatar;

  @NameJoinField(id = "policyId", repository = "policyRepo")
  private String name;

  private String description;

}
