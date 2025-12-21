package cloud.xcan.angus.core.gm.interfaces.tag.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Tag associated application VO
 */
@Data
@Schema(description = "标签关联的应用信息")
public class TagAppVo {

  @Schema(description = "应用ID")
  private Long id;

  @Schema(description = "应用名称")
  private String name;
}
