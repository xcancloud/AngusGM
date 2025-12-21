package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * Available permission VO
 */
@Data
@Schema(description = "可用权限资源")
public class AvailablePermissionVo {

  @Schema(description = "资源标识")
  private String resource;

  @Schema(description = "资源名称")
  private String resourceName;

  @Schema(description = "资源描述")
  private String description;

  @Schema(description = "可用操作列表")
  private List<ActionVo> actions;

  @Data
  @Schema(description = "操作信息")
  public static class ActionVo {

    @Schema(description = "操作标识")
    private String action;

    @Schema(description = "操作名称")
    private String name;
  }
}
