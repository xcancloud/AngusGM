package cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.enums.EventType;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;


@Setter
@Getter
public class EventTemplateReplaceDto implements Serializable {

  @Schema(description = "Event template identifier for updating existing template. Leave empty to create new template")
  private Long id;

  @NotBlank
  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "Application code that owns the event template", requiredMode = RequiredMode.REQUIRED)
  private String appCode;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Unique event code for the template", requiredMode = RequiredMode.REQUIRED)
  private String eventCode;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Display name for the event template", requiredMode = RequiredMode.REQUIRED)
  private String eventName;

  @NotNull
  @Schema(description = "Event type classification", requiredMode = RequiredMode.REQUIRED)
  private EventType eventType;

  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "Target resource type for the event")
  private String targetType;

  @NotNull
  @Schema(description = "Whether this is a private edition template", requiredMode = RequiredMode.REQUIRED)
  private Boolean private0;

  @Schema(description = "Supported notification channels for this event template")
  private Set<ReceiveChannelType> allowedChannelTypes;

}
