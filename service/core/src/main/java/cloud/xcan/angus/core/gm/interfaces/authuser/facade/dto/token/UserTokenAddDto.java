package cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.token;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.commonlink.AASConstant;
import cloud.xcan.angus.validator.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Setter
@Getter
@Accessors(chain = true)
public class UserTokenAddDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(example = "User access token name. The identifier must be unique.",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String name;

  /**
   * @see AASConstant#DEFAULT_TOKEN_EXPIRE_SECOND
   */
  @Future
  @Schema(description = "Access token expire date, valid within `24` hours by default.",
      example = "2025-06-28 19:00:00")
  private LocalDateTime expiredDate;

  @Password
  @NotNull
  @Schema(example = "Current user password.", requiredMode = RequiredMode.REQUIRED)
  private String password;

}
