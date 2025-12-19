package cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Create tenant DTO
 */
@Data
@Schema(description = "创建租户请求参数")
public class TenantCreateDto {

  @NotBlank
  @Size(max = 100)
  @Schema(description = "租户名称", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Size(max = 50)
  @Schema(description = "租户编码", requiredMode = RequiredMode.REQUIRED)
  private String code;

  @Schema(description = "租户类型（个人/企业）")
  private String type;

  @Schema(description = "账号类型（main/sub）")
  private String accountType;

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

  @Schema(description = "状态（已启用/已禁用）")
  private String status;

  @Size(max = 500)
  @Schema(description = "Logo URL")
  private String logo;
}
