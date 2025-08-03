package cloud.xcan.angus.api.gm.tenant.dto.audit;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_REMARK_LENGTH;

import cloud.xcan.angus.api.enums.TenantRealNameStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class TenantRealNameAuditDto implements Serializable {

  @NotNull
  @Schema(description = "Tenant identifier for real name audit. Used for identifying the specific tenant for audit processing", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @NotNull
  @Schema(description = "Tenant real name audit status for approval decision. Used for determining the audit result and next steps", example = "AUDITED", allowableValues = "AUDITED,FAILED_AUDIT",
      requiredMode = RequiredMode.REQUIRED)
  private TenantRealNameStatus status;

  @Length(max = MAX_REMARK_LENGTH)
  @Schema(description = "Audit reason for approval or rejection decision. Used for providing feedback on the audit result", example = "Reason for tenant's real name approval or rejection")
  private String reason;

}
