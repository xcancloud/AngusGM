package cloud.xcan.angus.core.gm.interfaces.operation.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OperationFindDto extends PageQuery {

  private Long id;

  private String clientId;

  private String requestId;

  private String resourceName;

  private Long tenantId;

  private Long userId;

  private Boolean success;

  private LocalDateTime optDate;

}
