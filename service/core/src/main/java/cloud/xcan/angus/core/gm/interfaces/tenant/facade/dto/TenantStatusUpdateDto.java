package cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto;

import cloud.xcan.angus.core.gm.domain.tenant.enums.TenantStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "租户状态更新请求参数")
public class TenantStatusUpdateDto {

  @NotNull
  @Schema(description = "状态", requiredMode = RequiredMode.REQUIRED)
  private TenantStatus status;
}
