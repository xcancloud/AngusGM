package cloud.xcan.angus.core.gm.interfaces.policy.facade.dto;

import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyEffect;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

/**
 * <p>Create role DTO</p>
 */
@Data
@Schema(description = "创建角色请求参数")
public class PolicyCreateDto {

  @NotBlank
  @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Schema(description = "角色编码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String code;

  @Schema(description = "描述")
  private String description;

  @Schema(description = "应用ID")
  private String appId;

  @Schema(description = "是否设为默认角色")
  private Boolean isDefault = false;

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
