package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo;


import cloud.xcan.angus.api.commonlink.policy.PolicyGrantStage;
import cloud.xcan.angus.api.commonlink.policy.PolicyType;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.remote.NameJoinField;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class AuthPolicyVo {

  private Long id;

  private String name;

  private String code;

  private PolicyType type;

  private String description;

  private Boolean default0;

  private PolicyGrantStage grantStage;

  private Long appId;

  private String appName;

  private String appVersion;

  private EditionType appEditionType;

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
  private String creator;

  private LocalDateTime createdDate;

}
