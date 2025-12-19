package cloud.xcan.angus.api.gm.tenant.dto;

import cloud.xcan.angus.api.enums.TenantRealNameStatus;
import cloud.xcan.angus.api.enums.TenantStatus;
import cloud.xcan.angus.api.enums.TenantType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class TenantFindDto extends PageQuery {

  @Schema(description = "Tenant identifier for precise tenant lookup. Used for specific tenant identification and retrieval")
  private Long id;

  @Schema(description = "Tenant name for search and filtering. Used for tenant identification and display")
  private String name;

  @Schema(description = "Tenant number for unique identification. Used for tenant lookup and reference")
  private String no;

  @Schema(description = "Tenant type for classification filtering. Used for tenant categorization and management")
  private TenantType type;

  @Schema(description = "Tenant status for lifecycle filtering. Used for tenant state management and filtering")
  private TenantStatus status;

  @Schema(description = "Tenant real name status for compliance filtering. Used for real name verification status tracking")
  private TenantRealNameStatus realNameStatus;

  @Schema(description = "Tenant lock status for security filtering. Used for account security and access control")
  private Boolean locked;

  @Schema(description = "Tenant cancellation application date for lifecycle filtering. Used for tenant termination tracking")
  private LocalDateTime applyCancelDate;

  @Schema(description = "Tenant last lock date for security tracking. Used for account security monitoring and reporting")
  private LocalDateTime lastLockDate;

  @Schema(description = "Tenant lock start date for security filtering. Used for account lock period tracking")
  private LocalDateTime lockStartDate;

  @Schema(description = "Tenant lock end date for security filtering. Used for account lock period tracking")
  private LocalDateTime lockEndDate;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }

}
