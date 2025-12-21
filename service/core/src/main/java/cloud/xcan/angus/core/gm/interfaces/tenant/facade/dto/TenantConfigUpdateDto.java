package cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "租户配置更新请求参数")
public class TenantConfigUpdateDto {

  @Min(1)
  @Schema(description = "最大用户数")
  private Integer maxUsers;

  @Schema(description = "最大存储空间")
  private String maxStorage;

  @Min(1)
  @Schema(description = "最大部门数")
  private Integer maxDepartments;

  @Schema(description = "功能特性列表")
  private List<String> features;

  @Schema(description = "自定义域名")
  private String customDomain;
}
