package cloud.xcan.angus.core.gm.interfaces.api.facade.dto;

import cloud.xcan.angus.api.enums.ApiType;
import cloud.xcan.angus.remote.PageQuery;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ApiLogFindDto extends PageQuery {

  private Long id;

  private String clientId;

  private String requestId;

  private Long tenantId;

  private Long userId;

  private Boolean success;

  private String serviceCode;

  private String instanceId;

  private String apiCode;

  private ApiType apiType;

  private String resourceName;

  private String method;

  private String uri;

  private Integer status;

  private LocalDateTime requestDate;

}
