package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Group status update response VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class GroupStatusUpdateVo implements Serializable {

  private Long id;

  private String status;

  private LocalDateTime modifiedDate;
}
