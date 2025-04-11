package cloud.xcan.angus.core.gm.interfaces.event.facade.dto;

import cloud.xcan.angus.api.enums.EventType;
import cloud.xcan.angus.core.gm.domain.event.push.EventPushStatus;
import cloud.xcan.angus.remote.OrderSort;
import cloud.xcan.angus.remote.PageQuery;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class EventSearchDto extends PageQuery {

  private Long id;

  private String code;

  private String eKey;

  private EventType type;

  private Long userId;

  private Long tenantId;

  private String targetId;

  private String targetType;

  private String targetName;

  private String appCode;

  private String serviceCode;

  private LocalDateTime createdDate;

  private EventPushStatus pushStatus;

  private Boolean nonTenantEvent;

  @Override
  public OrderSort getDefaultOrderSort() {
    return OrderSort.DESC;
  }

  public String getDefaultOrderBy() {
    return "createdDate";
  }

}
