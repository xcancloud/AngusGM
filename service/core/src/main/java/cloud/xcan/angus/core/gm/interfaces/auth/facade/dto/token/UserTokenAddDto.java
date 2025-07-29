package cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.token;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.commonlink.AuthConstant;
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
  @Schema(description = "User access token display name. Must be unique identifier", requiredMode = RequiredMode.REQUIRED)
  private String name;

  /**
   * @see AuthConstant#DEFAULT_TOKEN_EXPIRE_SECOND
   */
  @Future
  @Schema(description = "Access token expiration date. Valid for 24 hours by default", example = "2025-06-28 19:00:00")
  private LocalDateTime expiredDate;

  @Password
  @NotNull
  @Schema(description = "Current user password for verification", requiredMode = RequiredMode.REQUIRED)
  private String password;

}
