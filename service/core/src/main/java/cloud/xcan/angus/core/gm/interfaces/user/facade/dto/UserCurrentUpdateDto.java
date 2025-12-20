package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Update current user DTO
 */
@Data
@Schema(description = "更新当前用户信息请求参数")
public class UserCurrentUpdateDto {

  @Size(max = 50)
  @Schema(description = "姓名")
  private String name;

  @Size(max = 500)
  @Schema(description = "头像URL")
  private String avatar;

  @Size(max = 20)
  @Schema(description = "手机号")
  private String phone;
}
