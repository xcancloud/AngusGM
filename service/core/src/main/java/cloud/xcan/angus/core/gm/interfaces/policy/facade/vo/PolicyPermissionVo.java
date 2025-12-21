package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * Policy permission VO
 */
@Data
@Schema(description = "角色权限配置")
public class PolicyPermissionVo {

  @Schema(description = "角色ID")
  private Long roleId;

  @Schema(description = "角色名称")
  private String roleName;

  @Schema(description = "权限列表")
  private List<PermissionItemVo> permissions;

  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;

  @Data
  @Schema(description = "权限项")
  public static class PermissionItemVo {

    @Schema(description = "资源标识")
    private String resource;

    @Schema(description = "资源名称")
    private String resourceName;

    @Schema(description = "操作列表")
    private List<String> actions;

    @Schema(description = "描述")
    private String description;
  }
}
