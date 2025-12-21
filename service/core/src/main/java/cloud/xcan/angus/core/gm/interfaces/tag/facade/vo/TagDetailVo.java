package cloud.xcan.angus.core.gm.interfaces.tag.facade.vo;

import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
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

  @Schema(description = "描述")
  private String description;

  @Schema(description = "是否系统标签")
  private Boolean isSystem;

  @Schema(description = "使用次数")
  private Long usageCount;

  @Schema(description = "关联的应用列表")
  private List<TagAppVo> applications;
}
