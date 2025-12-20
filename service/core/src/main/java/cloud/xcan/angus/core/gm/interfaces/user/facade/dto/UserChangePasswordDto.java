package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Change password DTO (for current user)
 */
@Data
@Schema(description = "修改密码请求参数")
public class UserChangePasswordDto {

  @NotBlank
  @Size(max = 255)
  @Schema(description = "原密码", requiredMode = RequiredMode.REQUIRED)
  private String oldPassword;

  @NotBlank
  @Size(max = 255)
  @Schema(description = "新密码", requiredMode = RequiredMode.REQUIRED)
  private String newPassword;

  @NotBlank
  @Size(max = 255)
  @Schema(description = "确认密码", requiredMode = RequiredMode.REQUIRED)
  private String confirmPassword;
}
