package cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto;

import cloud.xcan.angus.core.gm.domain.tenant.enums.TenantStatus;
import cloud.xcan.angus.core.gm.domain.tenant.enums.TenantType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Update tenant DTO
 */
@Data
@Schema(description = "更新租户请求参数")
public class TenantUpdateDto {

  @Size(max = 100)
  @Schema(description = "租户名称")
  private String name;

  @Schema(description = "租户类型")
  private TenantType type;

  @Size(max = 50)
  @Schema(description = "管理员姓名")
  private String adminName;

  @Size(max = 100)
  @Schema(description = "管理员邮箱")
  private String adminEmail;

  @Size(max = 20)
  @Schema(description = "管理员电话")
  private String adminPhone;

  @Size(max = 500)
  @Schema(description = "地址")
  private String address;

  @Schema(description = "过期日期（yyyy-MM-dd）")
  private String expireDate;

  @Schema(description = "状态")
  private TenantStatus status;

  @Size(max = 500)
  @Schema(description = "Logo URL")
  private String logo;
}
