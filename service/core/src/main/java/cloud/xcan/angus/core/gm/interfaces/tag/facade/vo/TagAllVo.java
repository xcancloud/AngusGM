package cloud.xcan.angus.core.gm.interfaces.tag.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Tag all list VO (simplified for /all endpoint)
 */
@Data
@Schema(description = "标签简要信息")
public class TagAllVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "标签名称")
  private String name;

  @Schema(description = "描述")
  private String description;

  @Schema(description = "是否系统标签")
  private Boolean isSystem;
}
