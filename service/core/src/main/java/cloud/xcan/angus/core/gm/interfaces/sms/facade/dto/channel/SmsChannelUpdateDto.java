package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.channel;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X2;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class SmsChannelUpdateDto implements Serializable {

  @NotNull
  @Schema(description = "Sms channel id", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "The Api accessKey secret of sms channel", maxLength = MAX_CODE_LENGTH_X2)
  private String accessKeySecret;

  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "The Api accessKey id of sms channel", maxLength = MAX_CODE_LENGTH_X2)
  private String accessKeyId;

  @Length(max = MAX_URL_LENGTH)
  @Schema(description = "The Api endpoint of sms channel", maxLength = MAX_URL_LENGTH)
  private String endpoint;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Third sms channel no", maxLength = MAX_CODE_LENGTH)
  private String thirdChannelNo;

}
