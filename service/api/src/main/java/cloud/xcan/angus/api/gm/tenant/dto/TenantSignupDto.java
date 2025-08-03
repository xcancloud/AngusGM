package cloud.xcan.angus.api.gm.tenant.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ADDRESS_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_REMARK_LENGTH;

import cloud.xcan.angus.api.enums.TenantRealNameStatus;
import cloud.xcan.angus.api.enums.TenantType;
import cloud.xcan.angus.api.enums.UserSource;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Setter
@Getter
@Accessors(chain = true)
public class TenantSignupDto implements Serializable {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Tenant name for identification and display. Used for tenant management and organization", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotNull
  @Schema(description = "Tenant type for classification and management. Used for determining tenant category and applicable policies", requiredMode = RequiredMode.REQUIRED)
  private TenantType type;

  @NotNull
  @Schema(description = "Tenant source for registration tracking. Used for identifying the registration channel and source", requiredMode = RequiredMode.REQUIRED)
  private UserSource source;

  @Length(max = MAX_ADDRESS_LENGTH)
  @Schema(description = "Tenant contact address for location information. Used for contact details and regional compliance")
  private String address;

  @Length(max = MAX_REMARK_LENGTH)
  @Schema(description = "Tenant remark for additional information. Used for tenant documentation and management notes")
  private String remark;

  @NotNull
  @Schema(description = "Tenant real name audit status for compliance tracking. Used for real name verification status management", requiredMode = RequiredMode.REQUIRED)
  private TenantRealNameStatus auditStatus;

}
