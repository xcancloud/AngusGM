package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Service instance status update response VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class ServiceInstanceStatusVo implements Serializable {

  private String instanceId;

  private String status;

  private LocalDateTime modifiedDate;
}
