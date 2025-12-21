package cloud.xcan.angus.core.gm.interfaces.group.facade.dto;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Group member remove DTO
 */
@Getter
@Setter
@Accessors(chain = true)
public class GroupMemberRemoveDto implements Serializable {

  @NotEmpty
  private List<Long> userIds;
}
