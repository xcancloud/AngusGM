package cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo;

import cloud.xcan.angus.core.gm.domain.tenant.enums.TenantStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "租户状态更新响应")
public class TenantStatusUpdateVo {

  @Schema(description = "租户ID")
  private Long id;

  @Schema(description = "状态")
  private TenantStatus status;

  @Schema(description = "更新时间")
  private LocalDateTime modifiedDate;
}
