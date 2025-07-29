package cloud.xcan.angus.core.gm.interfaces.service.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X5;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_OPENAPI_DOC_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_OPENAPI_SUMMARY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URI_LENGTH;

import cloud.xcan.angus.api.enums.ApiType;
import cloud.xcan.angus.spec.http.HttpMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class ServiceApiAddDto {

  @NotNull
  @Schema(description = "ID of the service that this API belongs to", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long serviceId;

  @NotEmpty
  @Length(max = MAX_OPENAPI_SUMMARY_LENGTH)
  @Schema(description = "Human-readable name or summary of the API endpoint", example = "Create User Account", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotEmpty
  @Length(max = MAX_CODE_LENGTH_X5)
  @Schema(description = "Unique operation identifier or API code", example = "user:create", requiredMode = RequiredMode.REQUIRED)
  private String code;

  @NotEmpty
  @Length(max = MAX_URI_LENGTH)
  @Schema(description = "HTTP endpoint URI path", example = "/api/v1/users", requiredMode = RequiredMode.REQUIRED)
  private String uri;

  @NotNull
  @Schema(description = "HTTP request method for this API endpoint", example = "POST", requiredMode = RequiredMode.REQUIRED)
  private HttpMethod method;

  @Schema(description = "Angus platform API type classification. Auto-determined from URI if not specified", example = "API")
  private ApiType type;

  @Length(max = MAX_OPENAPI_DOC_DESC_LENGTH)
  @Schema(description = "Detailed description of the API functionality, parameters, and response")
  private String description;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Resource name or OpenAPI tag for grouping related APIs. Auto-determined from URI if not specified", example = "user")
  private String resourceName;

  @Schema(description = "Whether this API endpoint is enabled and accessible", defaultValue = "true")
  private Boolean enabled;

}
