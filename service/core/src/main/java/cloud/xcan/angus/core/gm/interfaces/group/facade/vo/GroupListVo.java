package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Group list VO
 */
@Data
@Schema(description = "组列表项")
public class GroupListVo extends GroupDetailVo {
  // Inherits all fields from GroupDetailVo
}
