package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Group owner update response VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class GroupOwnerUpdateVo implements Serializable {

  private Long groupId;

  private Long ownerId;

  private String ownerName;

  private LocalDateTime modifiedDate;
}
