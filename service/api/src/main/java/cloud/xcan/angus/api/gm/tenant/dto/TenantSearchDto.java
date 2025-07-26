package cloud.xcan.angus.api.gm.tenant.dto;

import cloud.xcan.angus.api.enums.TenantRealNameStatus;
import cloud.xcan.angus.api.enums.TenantStatus;
import cloud.xcan.angus.api.enums.TenantType;
import cloud.xcan.angus.remote.PageQuery;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class TenantSearchDto extends PageQuery {

  private Long id;

  private String name;

  private String no;

  private TenantType type;

  private TenantStatus status;

  private TenantRealNameStatus realNameStatus;

  private Boolean locked;

  private LocalDateTime createdDate;

  private LocalDateTime applyCancelDate;

  private LocalDateTime lastLockDate;

  private LocalDateTime lockStartDate;

  private LocalDateTime lockEndDate;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
