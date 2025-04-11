package cloud.xcan.angus.core.gm.interfaces.operation.facade.vo;

import cloud.xcan.angus.core.gm.domain.operation.OperationResourceType;
import cloud.xcan.angus.core.gm.domain.operation.OperationType;
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

  private String requestId;

  private String clientId;

  private OperationResourceType resource;

  private String resourceName;

  private String resourceId;

  private OperationType type;

  private Long userId;

  private String fullName;

  private LocalDateTime optDate;

  private String description;

  private String detail;

  private Boolean private0;

  private Long tenantId;

  private String tenantName;

}
