package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import cloud.xcan.angus.core.gm.domain.user.enums.EnableStatus;
import cloud.xcan.angus.core.gm.domain.user.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

/**
 * Update user DTO
 */
@Data
@Schema(description = "更新用户请求参数")
public class UserUpdateDto {

  @Size(max = 50)
  @Schema(description = "姓名")
  private String name;

  @Email
  @Size(max = 100)
  @Schema(description = "邮箱")
  private String email;

  @Size(max = 20)
  @Schema(description = "手机号")
  private String phone;

  @Size(max = 500)
  @Schema(description = "头像URL")
  private String avatar;

  @Schema(description = "部门ID")
  private Long departmentId;

  @Schema(description = "角色ID列表")
  private List<Long> roleIds;

  @Schema(description = "状态")
  private UserStatus status;

  @Schema(description = "启用状态")
  private EnableStatus enableStatus;
}
