package cloud.xcan.angus.core.gm.interfaces.user.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * User list VO
 */
@Data
@Schema(description = "用户列表项")
public class UserListVo extends UserDetailVo {
  // Inherits all fields from UserDetailVo
}
