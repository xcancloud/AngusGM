package cloud.xcan.angus.core.gm.interfaces.tag.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Tag list VO
 */
@Data
@Schema(description = "标签列表项")
public class TagListVo extends TagDetailVo {
  // Inherits all fields from TagDetailVo
}
