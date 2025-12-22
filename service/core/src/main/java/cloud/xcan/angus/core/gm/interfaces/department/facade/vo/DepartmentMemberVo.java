package cloud.xcan.angus.core.gm.interfaces.department.facade.vo;

import cloud.xcan.angus.remote.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "部门成员")
public class DepartmentMemberVo extends TenantAuditingVo {

  @Schema(description = "用户ID")
  private Long id;

  @Schema(description = "用户名称")
  private String name;

  @Schema(description = "邮箱")
  private String email;

  @Schema(description = "电话")
  private String phone;

  @Schema(description = "头像")
  private String avatar;

  @Schema(description = "角色")
  private String role;

  @Schema(description = "状态")
  private String status;

  @Schema(description = "是否为负责人")
  private Boolean isManager;

  @Schema(description = "加入日期")
  private String joinDate;
}
