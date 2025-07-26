package cloud.xcan.angus.api.gm.tenant.dto.audit;

import cloud.xcan.angus.api.commonlink.tenant.cert.EnterpriseCert;
import cloud.xcan.angus.api.commonlink.tenant.cert.EnterpriseLegalPersonCert;
import cloud.xcan.angus.api.commonlink.tenant.cert.GovernmentCert;
import cloud.xcan.angus.api.commonlink.tenant.cert.PersonalCert;
import cloud.xcan.angus.api.enums.TenantType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class TenantAuditUpdateDto implements Serializable {

  @NotNull
  @Schema(description = "Tenant id.", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Schema(description = "Tenant real name type.")
  private TenantType type;

  @Valid
  @Schema(description = "Personal tenant authentication information.")
  private PersonalCert personalCert;

  @Valid
  @Schema(description = "Enterprise tenant authentication information.")
  private EnterpriseCert enterpriseCert;

  @Valid
  @Schema(description = "Enterprise legal person authentication information.")
  private EnterpriseLegalPersonCert enterpriseLegalPersonCert;

  @Valid
  @Schema(description = "Government tenant authentication information.")
  private GovernmentCert governmentCert;

}
