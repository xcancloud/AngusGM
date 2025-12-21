package cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto;

import cloud.xcan.angus.api.enums.TenantRealNameStatus;
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
  @Schema(description = "Tenant identifier for real name verification", requiredMode = RequiredMode.REQUIRED)
  private Long tenantId;

  @NotNull
  @Schema(description = "Tenant real name verification status", requiredMode = RequiredMode.REQUIRED)
  private TenantRealNameStatus realNameStatus;

}
