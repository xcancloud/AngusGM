package cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template;

import cloud.xcan.angus.api.enums.EventType;
import cloud.xcan.angus.remote.OrderSort;
import cloud.xcan.angus.remote.PageQuery;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EventTemplateSearchDto extends PageQuery {

  private Long id;

  private String eventCode;

  private String eventName;

  private EventType eventType;

  private String eKey;

  private String appCode;

  private Long createdBy;

  private LocalDateTime createdDate;

  public OrderSort getDefaultOrderSort() {
    return OrderSort.ASC;
  }

}
