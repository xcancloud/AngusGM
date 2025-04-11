package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.channel;

import static cloud.xcan.angus.api.commonlink.SmsConstants.MAX_MOBILE_SIZE;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class SmsChannelTestSendDto implements Serializable {

  @NotNull
  @Schema(description = "Sms channel id.", example = "Test Channel Id", requiredMode = RequiredMode.REQUIRED)
  private Long channelId;

  @NotNull
  @Size(max = MAX_MOBILE_SIZE)
  @Schema(description = "Receive SMS mobiles.", requiredMode = RequiredMode.REQUIRED)
  private List<String> mobiles;

}
