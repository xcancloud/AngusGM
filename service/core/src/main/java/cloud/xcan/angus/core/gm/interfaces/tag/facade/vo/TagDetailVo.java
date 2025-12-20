package cloud.xcan.angus.core.gm.interfaces.tag.facade.vo;

import cloud.xcan.angus.core.gm.domain.tag.enums.TagStatus;
import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Tag detail VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "标签详情")
public class TagDetailVo extends TenantAuditingVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "标签名称")
  private String name;

  @Schema(description = "颜色")
  private String color;

  @Schema(description = "分类")
  private String category;

  @Schema(description = "描述")
  private String description;

  @Schema(description = "状态")
  private TagStatus status;

  @Schema(description = "排序")
  private Integer sortOrder;

  @Schema(description = "使用次数")
  private Long usageCount;
}
