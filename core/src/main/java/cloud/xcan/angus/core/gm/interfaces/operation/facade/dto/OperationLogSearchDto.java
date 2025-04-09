package cloud.xcan.angus.core.gm.interfaces.operation.facade.dto;

import cloud.xcan.angus.core.gm.domain.operation.OperationResourceType;
import cloud.xcan.angus.core.gm.domain.operation.OperationType;
import cloud.xcan.angus.remote.PageQuery;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OperationLogSearchDto extends PageQuery {

  private Long id;

  private String requestId;

  private String clientId;

  private OperationResourceType resource;

  private String resourceId;

  private OperationType type;

  private Long userId;

  private LocalDateTime optDate;

  private String description;

  private String detail;

  private Boolean private0;

  private Long tenantId;

}
