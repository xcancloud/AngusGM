package cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo;

import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Tenant detail VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "租户详情")
public class TenantDetailVo extends TenantAuditingVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "租户名称")
  private String name;

  @Schema(description = "租户编码")
  private String code;

  @Schema(description = "租户类型")
  private String type;

  @Schema(description = "账号类型")
  private String accountType;

  @Schema(description = "管理员姓名")
  private String adminName;

  @Schema(description = "管理员邮箱")
  private String adminEmail;

  @Schema(description = "管理员电话")
  private String adminPhone;

  @Schema(description = "用户数量")
  private Long userCount;

  @Schema(description = "部门数量")
  private Long departmentCount;

  @Schema(description = "状态")
  private String status;

  @Schema(description = "地址")
  private String address;

  @Schema(description = "过期日期")
  private String expireDate;

  @Schema(description = "Logo")
  private String logo;
}
