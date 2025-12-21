package cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * Add roles to authorization result VO
 */
@Data
@Schema(description = "添加角色结果")
public class AuthorizationRoleAddVo {

  @Schema(description = "授权ID")
  private String authorizationId;

  @Schema(description = "添加的角色列表")
  private List<AddedRole> addedRoles;

  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;

  @Data
  @Schema(description = "添加的角色信息")
  public static class AddedRole {

    @Schema(description = "角色ID")
    private String id;

    @Schema(description = "角色名称")
    private String name;
  }
}
