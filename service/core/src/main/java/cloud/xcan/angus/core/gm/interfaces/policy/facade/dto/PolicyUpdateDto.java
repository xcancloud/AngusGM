package cloud.xcan.angus.core.gm.interfaces.policy.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import lombok.Data;

/**
 * <p>Update role DTO</p>
 */
@Data
@Schema(description = "更新角色请求参数")
public class PolicyUpdateDto {

  @Schema(description = "角色名称")
  private String name;

  @Schema(description = "描述")
  private String description;

  @Schema(description = "是否设为默认角色")
  private Boolean isDefault;

  @Valid
  @Schema(description = "权限列表")
  private List<PermissionDto> permissions;

  @Data
  @Schema(description = "权限项")
  public static class PermissionDto {

    @Schema(description = "资源标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private String resource;

    @Schema(description = "操作列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> actions;
  }
}
