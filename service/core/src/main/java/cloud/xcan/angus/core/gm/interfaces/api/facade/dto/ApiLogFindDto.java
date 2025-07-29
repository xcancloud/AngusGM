package cloud.xcan.angus.core.gm.interfaces.api.facade.dto;

import cloud.xcan.angus.api.enums.ApiType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ApiLogFindDto extends PageQuery {

  @Schema(description = "API log record identifier for filtering")
  private Long id;

  @Schema(description = "OAuth2 client identifier for filtering")
  private String clientId;

  @Schema(description = "API request identifier for filtering")
  private String requestId;

  @Schema(description = "Tenant identifier for filtering")
  private Long tenantId;

  @Schema(description = "User identifier for filtering")
  private Long userId;

  @Schema(description = "API request success status for filtering")
  private Boolean success;

  @Schema(description = "API service code for filtering")
  private String serviceCode;

  @Schema(description = "API service instance identifier for filtering")
  private String instanceId;

  @Schema(description = "API code for filtering")
  private String apiCode;

  @Schema(description = "Angus platform API type for filtering")
  private ApiType apiType;

  @Schema(description = "API resource name for filtering")
  private String resourceName;

  @Schema(description = "HTTP request method for filtering")
  private String method;

  @Schema(description = "API endpoint URI for filtering")
  private String uri;

  @Schema(description = "HTTP response status code for filtering")
  private Integer status;

  @Schema(description = "API request timestamp for filtering")
  private LocalDateTime requestDate;

}
