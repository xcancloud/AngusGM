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
  @Schema(description = "Tenant identifier for audit update. Used for identifying the specific tenant for audit modification", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Schema(description = "Tenant real name type for audit classification. Used for determining the type of authentication information to be audited")
  private TenantType type;

  @Valid
  @Schema(description = "Personal tenant authentication information for audit review. Used for personal identity verification and compliance checking")
  private PersonalCert personalCert;

  @Valid
  @Schema(description = "Enterprise tenant authentication information for audit review. Used for enterprise identity verification and compliance checking")
  private EnterpriseCert enterpriseCert;

  @Valid
  @Schema(description = "Enterprise legal person authentication information for audit review. Used for legal person identity verification and compliance checking")
  private EnterpriseLegalPersonCert enterpriseLegalPersonCert;

  @Valid
  @Schema(description = "Government tenant authentication information for audit review. Used for government identity verification and compliance checking")
  private GovernmentCert governmentCert;

}
