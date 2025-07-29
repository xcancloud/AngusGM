package cloud.xcan.angus.core.gm.interfaces.operation.facade.dto;

import cloud.xcan.angus.core.gm.domain.operation.OperationResourceType;
import cloud.xcan.angus.core.gm.domain.operation.OperationType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OperationLogFindDto extends PageQuery {

  @Schema(description = "Operation log identifier")
  private Long id;

  @Schema(description = "Unique request identifier for tracking")
  private String requestId;

  @Schema(description = "Client application identifier")
  private String clientId;

  @Schema(description = "Type of resource being operated on")
  private OperationResourceType resource;

  @Schema(description = "Identifier of the resource being operated on")
  private String resourceId;

  @Schema(description = "Type of operation performed")
  private OperationType type;

  @Schema(description = "User identifier who performed the operation")
  private Long userId;

  @Schema(description = "Timestamp when the operation was performed")
  private LocalDateTime optDate;

  @Schema(description = "Brief description of the operation")
  private String description;

  @Schema(description = "Detailed information about the operation")
  private String detail;

  @Schema(description = "Whether this operation log is private")
  private Boolean private0;

  @Schema(description = "Tenant identifier associated with the operation")
  private Long tenantId;

}
