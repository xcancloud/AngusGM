package cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "租户使用统计")
public class TenantUsageVo {

  @Schema(description = "用户使用情况")
  private UsageItemVo users;

  @Schema(description = "存储使用情况")
  private StorageUsageItemVo storage;

  @Schema(description = "部门使用情况")
  private UsageItemVo departments;

  @Schema(description = "API调用情况")
  private ApiUsageItemVo apiCalls;

  @Data
  @Schema(description = "使用情况项")
  public static class UsageItemVo {

    @Schema(description = "当前数量")
    private Long current;

    @Schema(description = "最大数量")
    private Long max;

    @Schema(description = "使用率(%)")
    private Double usage;
  }

  @Data
  @Schema(description = "存储使用情况")
  public static class StorageUsageItemVo {

    @Schema(description = "当前使用量")
    private String current;

    @Schema(description = "最大容量")
    private String max;

    @Schema(description = "使用率(%)")
    private Double usage;
  }

  @Data
  @Schema(description = "API调用情况")
  public static class ApiUsageItemVo {

    @Schema(description = "本月调用次数")
    private Long thisMonth;

    @Schema(description = "最大调用次数")
    private Long max;

    @Schema(description = "使用率(%)")
    private Double usage;
  }
}
