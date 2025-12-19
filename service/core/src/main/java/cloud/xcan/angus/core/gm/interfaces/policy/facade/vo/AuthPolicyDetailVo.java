package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo;


import cloud.xcan.angus.api.commonlink.policy.PolicyGrantStage;
import cloud.xcan.angus.api.commonlink.policy.PolicyType;
import cloud.xcan.angus.remote.NameJoinField;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class AuthPolicyDetailVo {

  private Long id;

  private String name;

  private String code;

  private PolicyType type;

  private Boolean default0;

  private PolicyGrantStage grantStage;

  private String description;

  private String clientId;

  private Long appId;

  @NameJoinField(id = "appId", repository = "appRepo")
  private String appName;

  private Boolean enabled;

  private Long tenantId;

  @NameJoinField(id = "tenantId", repository = "commonTenantRepo")
  private String tenantName;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String creator;

  private LocalDateTime createdDate;

  private Long modifiedBy;

  @NameJoinField(id = "modifiedBy", repository = "commonUserBaseRepo")
  private String modifier;

  private LocalDateTime modifiedDate;

}
