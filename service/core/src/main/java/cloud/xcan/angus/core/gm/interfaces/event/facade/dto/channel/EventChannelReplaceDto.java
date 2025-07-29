package cloud.xcan.angus.core.gm.interfaces.event.facade.dto.channel;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH_X20;

import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
public class EventChannelReplaceDto implements Serializable {

  @Schema(description = "Event channel identifier for updating existing channel. Leave empty to create new channel", example = "1")
  private Long id;

  @NotNull
  @Schema(description = "Event notification channel type", example = "WECHAT",
      requiredMode = RequiredMode.REQUIRED)
  private ReceiveChannelType channelType;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Display name for the event channel", example = "WeChat Work Notification",
      requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Length(max = MAX_URL_LENGTH_X20)
  @Schema(description = "Event channel endpoint URL. For EMAIL type, multiple addresses separated by commas",
      example = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=9e3b6ca1-2d3d-4cc2-8314-153871c7ccaf",
      requiredMode = RequiredMode.REQUIRED)
  private String address;

}
