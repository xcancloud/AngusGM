package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo;


import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.policy.PolicyGrantScope;
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
public class AuthPolicyAssociatedVo {

  private Long id;

  private String name;

  private String code;

  private PolicyType type;

  private String description;

  private Boolean default0;

  private Boolean currentDefault;

  private Boolean openAuth;

  private PolicyGrantScope grantScope;

  private PolicyGrantStage grantStage;

  private Long appId;

  @NameJoinField(id = "appId", repository = "appRepo")
  private String appName;

  private Boolean enabled;

  private Long authBy;

  @NameJoinField(id = "authBy", repository = "commonUserBaseRepo")
  private String authByName;

  private LocalDateTime authDate;

  private Long tenantId;

  //@NameJoinField(id = "tenantId", repository = "commonTenantRepo")
  //private String tenantName;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  private LocalDateTime createdDate;

  private Long orgId;

  private AuthOrgType orgType;

  private String orgName;

}
