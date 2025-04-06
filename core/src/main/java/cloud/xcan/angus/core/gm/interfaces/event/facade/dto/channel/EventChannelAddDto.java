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
public class EventChannelAddDto implements Serializable {

  @NotNull
  @Schema(description = "Event channel type.", example = "WECHAT", requiredMode = RequiredMode.REQUIRED)
  private ReceiveChannelType channelType;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Event channel name.", example = "WeChat Reboot",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Length(max = MAX_URL_LENGTH_X20)
  @Schema(description = "Event channel address. When it is `EMAIL` type, multiple addresses are separated by English commas.",
      example = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=9e3b6ca1-2d3d-4cc2-8314-153871c7ccaf",
      maxLength = MAX_URL_LENGTH_X20, requiredMode = RequiredMode.REQUIRED)
  private String address;

}
