package cloud.xcan.angus.core.gm.interfaces.user.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * Reset password VO
 */
@Data
@Schema(description = "重置密码响应")
public class UserResetPasswordVo {

  @Schema(description = "用户ID")
  private Long id;

  @Schema(description = "密码重置时间")
  private LocalDateTime passwordResetTime;

  @Schema(description = "是否已发送邮件")
  private Boolean emailSent;
}
