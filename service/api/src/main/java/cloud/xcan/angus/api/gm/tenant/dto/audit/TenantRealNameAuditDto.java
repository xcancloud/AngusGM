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
  @Schema(description = "Tenant id.", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @NotNull
  @Schema(description = "Tenant real name status.", example = "AUDITED", allowableValues = "AUDITED,FAILED_AUDIT",
      requiredMode = RequiredMode.REQUIRED)
  private TenantRealNameStatus status;

  @Length(max = MAX_REMARK_LENGTH)
  @Schema(example = "Reason for tenant's real name approval or rejection.", maxLength = MAX_REMARK_LENGTH)
  private String reason;

}
