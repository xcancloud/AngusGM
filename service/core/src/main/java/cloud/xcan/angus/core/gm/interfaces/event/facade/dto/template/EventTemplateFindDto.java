package cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template;

import cloud.xcan.angus.api.enums.EventType;
import cloud.xcan.angus.remote.OrderSort;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class EventTemplateFindDto extends PageQuery {

  @Schema(description = "Event template identifier")
  private Long id;

  @Schema(description = "Event code for filtering")
  private String eventCode;

  @Schema(description = "Event name for filtering")
  private String eventName;

  @Schema(description = "Event type classification")
  private EventType eventType;

  @Schema(description = "Event key for filtering")
  private String eKey;

  @Schema(description = "Application code for filtering")
  private String appCode;

  @Schema(description = "Creator identifier")
  private Long createdBy;

  @Schema(description = "Template creation timestamp")
  private LocalDateTime createdDate;

  public OrderSort getDefaultOrderSort() {
    return OrderSort.ASC;
  }
}
