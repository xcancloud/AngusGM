package cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "租户配置")
public class TenantConfigVo {

  @Schema(description = "最大用户数")
  private Integer maxUsers;

  @Schema(description = "最大存储空间")
  private String maxStorage;

  @Schema(description = "最大部门数")
  private Integer maxDepartments;

  @Schema(description = "功能特性列表")
  private List<String> features;

  @Schema(description = "自定义域名")
  private String customDomain;

  @Schema(description = "主题配置")
  private ThemeVo theme;

  @Schema(description = "更新时间")
  private LocalDateTime modifiedDate;

  @Data
  @Schema(description = "主题配置")
  public static class ThemeVo {

    @Schema(description = "主色调")
    private String primaryColor;

    @Schema(description = "Logo")
    private String logo;
  }
}
