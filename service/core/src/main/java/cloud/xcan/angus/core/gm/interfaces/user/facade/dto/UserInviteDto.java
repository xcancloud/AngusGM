package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Invite user DTO
 */
@Data
@Schema(description = "邀请用户请求参数")
public class UserInviteDto {

  @NotBlank
  @Email
  @Size(max = 100)
  @Schema(description = "邮箱", requiredMode = RequiredMode.REQUIRED)
  private String email;

  @Schema(description = "角色ID")
  private Long roleId;

  @Schema(description = "部门ID")
  private Long departmentId;

  @Size(max = 500)
  @Schema(description = "邀请消息")
  private String message;

  @Schema(description = "过期天数", defaultValue = "7")
  private Integer expireDays = 7;
}
