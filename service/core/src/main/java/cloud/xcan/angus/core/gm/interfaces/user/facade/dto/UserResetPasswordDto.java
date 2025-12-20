package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Reset user password DTO
 */
@Data
@Schema(description = "重置用户密码请求参数")
public class UserResetPasswordDto {

  @NotBlank
  @Size(max = 255)
  @Schema(description = "新密码", requiredMode = RequiredMode.REQUIRED)
  private String newPassword;

  @Schema(description = "是否发送邮件通知", defaultValue = "false")
  private Boolean sendEmail = false;
}
