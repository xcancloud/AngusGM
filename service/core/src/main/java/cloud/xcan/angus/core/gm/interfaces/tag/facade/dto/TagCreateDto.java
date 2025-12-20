package cloud.xcan.angus.core.gm.interfaces.tag.facade.dto;

import cloud.xcan.angus.core.gm.domain.tag.enums.TagStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Create tag DTO
 */
@Data
@Schema(description = "创建标签请求参数")
public class TagCreateDto {

  @NotBlank
  @Size(max = 50)
  @Schema(description = "标签名称", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @Size(max = 20)
  @Schema(description = "颜色")
  private String color;

  @Size(max = 50)
  @Schema(description = "分类")
  private String category;

  @Size(max = 500)
  @Schema(description = "描述")
  private String description;

  @Schema(description = "排序")
  private Integer sortOrder;

  @Schema(description = "状态")
  private TagStatus status;
}
