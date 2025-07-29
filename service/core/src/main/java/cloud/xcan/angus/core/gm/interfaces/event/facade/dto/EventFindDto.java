package cloud.xcan.angus.core.gm.interfaces.event.facade.dto;

import cloud.xcan.angus.api.enums.EventType;
import cloud.xcan.angus.core.gm.domain.event.push.EventPushStatus;
import cloud.xcan.angus.remote.OrderSort;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class EventFindDto extends PageQuery {

  @Schema(description = "Event identifier")
  private Long id;

  @Schema(description = "Event code for filtering")
  private String code;

  @Schema(description = "Event key for filtering")
  private String eKey;

  @Schema(description = "Event type classification")
  private EventType type;

  @Schema(description = "User identifier for filtering")
  private Long userId;

  @Schema(description = "Tenant identifier for filtering")
  private Long tenantId;

  @Schema(description = "Target resource identifier")
  private String targetId;

  @Schema(description = "Target resource type")
  private String targetType;

  @Schema(description = "Target resource name")
  private String targetName;

  @Schema(description = "Application code for filtering")
  private String appCode;

  @Schema(description = "Service code for filtering")
  private String serviceCode;

  @Schema(description = "Event creation timestamp")
  private LocalDateTime createdDate;

  @Schema(description = "Event push notification status")
  private EventPushStatus pushStatus;

  @Schema(description = "Whether this is a non-tenant specific event")
  private Boolean nonTenantEvent;

  @Override
  public OrderSort getDefaultOrderSort() {
    return OrderSort.DESC;
  }

  public String getDefaultOrderBy() {
    return "createdDate";
  }

}
