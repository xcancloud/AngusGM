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
public class TenantRealNameSubmitDto implements Serializable {

  @NotNull
  @Schema(description = "Tenant real name type for authentication submission. Used for determining the type of authentication information to be submitted", requiredMode = RequiredMode.REQUIRED)
  private TenantType type;

  @Valid
  @Schema(description = "Personal tenant authentication information for real name submission. Used for personal identity verification and compliance")
  private PersonalCert personalCert;

  @Valid
  @Schema(description = "Enterprise tenant authentication information for real name submission. Used for enterprise identity verification and compliance")
  private EnterpriseCert enterpriseCert;

  @Valid
  @Schema(description = "Enterprise legal person authentication information for real name submission. Used for legal person identity verification and compliance")
  private EnterpriseLegalPersonCert enterpriseLegalPersonCert;

  @Valid
  @Schema(description = "Government tenant authentication information for real name submission. Used for government identity verification and compliance")
  private GovernmentCert governmentCert;

}
