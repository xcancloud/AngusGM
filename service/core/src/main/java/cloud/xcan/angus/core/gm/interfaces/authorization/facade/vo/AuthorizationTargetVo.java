package cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * Target authorization VO
 */
@Data
@Schema(description = "目标授权信息")
public class AuthorizationTargetVo {

  @Schema(description = "目标类型")
  private String targetType;

  @Schema(description = "目标ID")
  private String targetId;

  @Schema(description = "目标名称")
  private String targetName;

  @Schema(description = "角色列表")
  private List<RoleInfo> roles;

  @Schema(description = "权限列表")
  private List<String> permissions;

  @Data
  @Schema(description = "角色信息")
  public static class RoleInfo {

    @Schema(description = "角色ID")
    private String id;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "角色编码")
    private String code;

    @Schema(description = "应用ID")
    private String appId;

    @Schema(description = "应用名称")
    private String appName;
  }
}
