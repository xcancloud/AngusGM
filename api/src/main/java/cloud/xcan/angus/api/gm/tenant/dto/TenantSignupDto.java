package cloud.xcan.angus.api.gm.tenant.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ADDRESS_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_REMARK_LENGTH;

import cloud.xcan.angus.api.commonlink.tenant.TenantRealNameStatus;
import cloud.xcan.angus.api.commonlink.tenant.TenantType;
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
  @Schema(description = "Tenant name.", maxLength = MAX_NAME_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotNull
  @Schema(description = "Tenant type.", requiredMode = RequiredMode.REQUIRED)
  private TenantType type;

  @NotNull
  @Schema(description = "Tenant source.", requiredMode = RequiredMode.REQUIRED)
  private UserSource source;

  @Length(max = MAX_ADDRESS_LENGTH)
  @Schema(description = "Tenant contact address.", maxLength = MAX_ADDRESS_LENGTH)
  private String address;

  @Length(max = MAX_REMARK_LENGTH)
  @Schema(description = "Tenant remark.", maxLength = MAX_REMARK_LENGTH)
  private String remark;

  @NotNull
  @Schema(description = "Tenant real name status.", requiredMode = RequiredMode.REQUIRED)
  private TenantRealNameStatus auditStatus;

}
