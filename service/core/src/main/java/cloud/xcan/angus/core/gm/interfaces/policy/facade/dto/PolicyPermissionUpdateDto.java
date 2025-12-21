package cloud.xcan.angus.core.gm.interfaces.policy.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * Policy permission update DTO
 */
@Data
@Schema(description = "更新角色权限请求参数")
public class PolicyPermissionUpdateDto {

  @Schema(description = "权限列表")
  private List<PermissionDto> permissions;

  @Data
  @Schema(description = "权限项")
  public static class PermissionDto {

    @Schema(description = "资源标识")
    private String resource;

    @Schema(description = "操作列表")
    private List<String> actions;
  }
}
