package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import cloud.xcan.angus.core.gm.domain.user.enums.EnableStatus;
import cloud.xcan.angus.core.gm.domain.user.enums.UserStatus;
import cloud.xcan.angus.spec.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Find user DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询用户请求参数")
public class UserFindDto extends PageQuery {

  @Schema(description = "搜索关键词（姓名、邮箱、手机号）")
  private String keyword;

  @Schema(description = "状态筛选")
  private UserStatus status;

  @Schema(description = "启用状态筛选")
  private EnableStatus enableStatus;

  @Schema(description = "角色筛选")
  private Long roleId;

  @Schema(description = "部门筛选")
  private Long departmentId;

  @Schema(description = "锁定状态")
  private Boolean isLocked;

  @Schema(description = "在线状态")
  private Boolean isOnline;

  @Schema(description = "排序字段", allowableValues = {"id", "createdDate", "modifiedDate", "name", "lastLogin"})
  private String orderBy = "createdDate";
}
