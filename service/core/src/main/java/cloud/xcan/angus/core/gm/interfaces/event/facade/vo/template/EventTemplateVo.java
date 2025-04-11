package cloud.xcan.angus.core.gm.interfaces.event.facade.vo.template;

import cloud.xcan.angus.api.enums.EventType;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class EventTemplateVo {

  @Schema(description = "Template id")
  private Long id;

  @Schema(description = "Event code")
  private String eventCode;

  @Schema(description = "Event name")
  private String eventName;

  @Schema(description = "Event type")
  private EventType eventType;

  @Schema(description = "Event application code")
  private String appCode;

  @Schema(description = "Business resource type")
  private String targetType;

  @Schema(description = "Only supported in privatized edition")
  private Boolean private0;

  @Schema(description = "Event receiving channel types")
  private Set<ReceiveChannelType> allowedChannelTypes;

}
