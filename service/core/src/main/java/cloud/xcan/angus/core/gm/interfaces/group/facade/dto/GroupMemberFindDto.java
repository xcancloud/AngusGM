package cloud.xcan.angus.core.gm.interfaces.group.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Group member find DTO
 */
@Getter
@Setter
@Accessors(chain = true)
public class GroupMemberFindDto extends PageQuery {

  @Schema(description = "Search keyword")
  private String keyword;
}
