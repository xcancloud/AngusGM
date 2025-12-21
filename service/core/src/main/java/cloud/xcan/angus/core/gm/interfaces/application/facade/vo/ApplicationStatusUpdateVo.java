package cloud.xcan.angus.core.gm.interfaces.application.facade.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Application status update response VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class ApplicationStatusUpdateVo implements Serializable {

  private Long id;

  private String status;

  private LocalDateTime modifiedDate;
}
