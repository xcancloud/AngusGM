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
public class EventTemplateAddDto implements Serializable {

  @NotBlank
  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "Application code of the event.", maxLength = MAX_KEY_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String appCode;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Template event code.", maxLength = MAX_CODE_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String eventCode;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Template event name.", maxLength = MAX_NAME_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String eventName;

  @NotNull
  @Schema(description = "Template event type.", requiredMode = RequiredMode.REQUIRED)
  private EventType eventType;

  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "Resource type of the event.", maxLength = MAX_KEY_LENGTH)
  private String targetType;

  @NotNull
  @Schema(description = "Whether or not privatized edition template flag.",
      requiredMode = RequiredMode.REQUIRED)
  private Boolean private0;

  @Schema(description = "Receiving channels allowed or supported by the event template.")
  private Set<ReceiveChannelType> allowedChannelTypes;

}
