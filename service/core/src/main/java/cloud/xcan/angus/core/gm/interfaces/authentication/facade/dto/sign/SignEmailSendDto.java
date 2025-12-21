package cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.sign;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.validator.CollectionCharLength;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SignEmailSendDto {

  @NotNull
  @Schema(description = "Email verification business type", example = "SIGNUP", requiredMode = RequiredMode.REQUIRED)
  private EmailBizKey bizKey;

  @NotEmpty
  @Size(max = 200)
  @CollectionCharLength(maxLength = 100)
  @Schema(description = "Email addresses for verification code delivery", type = "array",
      example = "[\"user1@xcan.cloud\",\"user2@xcan.cloud\"]", requiredMode = RequiredMode.REQUIRED)
  private Set<String> toAddress;

}
