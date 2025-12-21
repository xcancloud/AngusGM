package cloud.xcan.angus.core.gm.interfaces.tag.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Update tag DTO
 */
@Data
@Schema(description = "更新标签请求参数")
public class TagUpdateDto {

  @Size(max = 50)
  @Schema(description = "标签名称")
  private String name;

  @Size(max = 500)
  @Schema(description = "描述")
  private String description;
}
