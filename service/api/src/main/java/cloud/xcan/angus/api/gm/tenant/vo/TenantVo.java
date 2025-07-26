package cloud.xcan.angus.api.gm.tenant.vo;

import cloud.xcan.angus.api.enums.TenantRealNameStatus;
import cloud.xcan.angus.api.enums.TenantSource;
import cloud.xcan.angus.api.enums.TenantStatus;
import cloud.xcan.angus.api.enums.TenantType;
import cloud.xcan.angus.remote.NameJoinField;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class TenantVo implements Serializable {

  private Long id;

  private String no;

  private String name;

  private TenantType type;

  private TenantSource source;

  private TenantStatus status;

  private TenantRealNameStatus realNameStatus;

  private String address;

  private Long userCount;

  private boolean locked;

  private LocalDateTime lastLockDate;

  private LocalDateTime lockStartDate;

  private LocalDateTime lockEndDate;

  private String remark;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  private LocalDateTime createdDate;

  private Long lastModifiedBy;

  @NameJoinField(id = "lastModifiedBy", repository = "commonUserBaseRepo")
  private String lastModifiedByName;

  private LocalDateTime lastModifiedDate;

}
