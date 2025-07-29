package cloud.xcan.angus.core.gm.interfaces.api.facade.dto;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.api.enums.ApiType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
@Accessors(chain = true)
public class ApiFindDto extends PageQuery {

  @Schema(description = "API identifier for filtering")
  private Long id;

  @Schema(description = "API summary or display name for filtering")
  private String name;

  @Schema(description = "API unique code or OpenAPI operation identifier for filtering")
  private String operationId;

  @Schema(description = "API service identifier for filtering")
  private Long serviceId;

  @Schema(description = "API service display name for filtering")
  private String serviceName;

  @Schema(description = "API enabled status for filtering")
  private Boolean enabled;

  @Schema(description = "Angus platform API type for filtering")
  private ApiType type;

  @DateTimeFormat(pattern = DATE_FMT)
  @Schema(description = "API creation date for filtering")
  private LocalDateTime createdDate;

}
