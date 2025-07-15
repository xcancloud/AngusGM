package cloud.xcan.angus.core.gm.interfaces.auth.facade.dto;

import cloud.xcan.angus.api.commonlink.tenant.TenantRealNameStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AuthUserRealNameUpdateDto {

  @NotNull
  @Schema(description = "Real name tenant id", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long tenantId;

  @NotNull
  @Schema(description = "Tenant real name status", requiredMode = RequiredMode.REQUIRED)
  private TenantRealNameStatus realNameStatus;

}
