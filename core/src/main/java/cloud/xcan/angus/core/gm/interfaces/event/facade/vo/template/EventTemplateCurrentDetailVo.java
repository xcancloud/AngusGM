package cloud.xcan.angus.core.gm.interfaces.event.facade.vo.template;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH;

import cloud.xcan.angus.api.enums.EventType;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Setter
@Getter
@Accessors(chain = true)
public class EventTemplateCurrentDetailVo {

  @Schema(description = "Template id")
  private Long id;

  @Schema(description = "Event code")
  private String eventCode;

  @Schema(description = "Event name")
  private String eventName;

  @Schema(description = "Event type")
  private EventType eventType;

  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "Error key, required when EventType exceptional flag is true")
  private String eKey;

  @Schema(description = "Event application code")
  private String appCode;

  @Schema(description = "Business resource type")
  private String targetType;

  @Schema(description = "Only supported in privatized edition")
  private Boolean private0;

  @Schema(description = "Event receiving channel types")
  private Set<ReceiveChannelType> allowedChannelTypes;

  @Schema(description = "Template receiving setting")
  private EventTemplateReceiveVo receiveSetting;

}
