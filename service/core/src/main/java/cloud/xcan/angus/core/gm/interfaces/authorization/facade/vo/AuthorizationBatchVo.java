package cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * Batch authorization result VO
 */
@Data
@Schema(description = "批量授权结果")
public class AuthorizationBatchVo {

  @Schema(description = "目标类型")
  private String targetType;

  @Schema(description = "目标数量")
  private Integer targetCount;

  @Schema(description = "角色数量")
  private Integer roleCount;

  @Schema(description = "成功数量")
  private Integer successCount;

  @Schema(description = "失败数量")
  private Integer failedCount;

  @Schema(description = "处理结果列表")
  private List<BatchResultItem> results;

  @Data
  @Schema(description = "单条处理结果")
  public static class BatchResultItem {

    @Schema(description = "目标ID")
    private Long targetId;

    @Schema(description = "目标名称")
    private String targetName;

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "错误信息")
    private String errorMessage;
  }
}
