package cloud.xcan.angus.core.gm.interfaces.operation.facade.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class OperationLogVo implements Serializable {

  private Long id;

  private String clientId;

  private String requestId;

  private String resourceName;

  private Long userId;

  private String fullname;

  private String description;

  private Boolean success;

  private String failureReason;

  private LocalDateTime optDate;

  private Long tenantId;

  private String tenantName;

}
