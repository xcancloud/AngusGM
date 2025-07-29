package cloud.xcan.angus.core.gm.interfaces.api.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_OPENAPI_DOC_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_OPENAPI_SUMMARY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URI_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URI_LENGTH_X4;

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
public class ApiAddDto {

  @NotNull
  @Schema(description = "API service identifier. Note: Modification is not allowed after creation", requiredMode = RequiredMode.REQUIRED)
  private Long serviceId;

  @NotEmpty
  @Length(max = MAX_OPENAPI_SUMMARY_LENGTH)
  @Schema(description = "API summary or display name", example = "Add user api", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotEmpty
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "API unique code or OpenAPI operation identifier", example = "user:add", requiredMode = RequiredMode.REQUIRED)
  private String operationId;

  @NotEmpty
  @Length(max = MAX_URI_LENGTH)
  @Schema(description = "API HTTP endpoint URI", example = "/api/v1/user", requiredMode = RequiredMode.REQUIRED)
  private String uri;

  @NotNull
  @Schema(description = "API HTTP request method", example = "POST", requiredMode = RequiredMode.REQUIRED)
  private HttpMethod method;

  @Schema(description = "Angus platform API type. If empty, automatically determined based on URI", example = "API")
  private ApiType type;

  @Length(max = MAX_OPENAPI_DOC_DESC_LENGTH)
  @Schema(description = "API detailed description for documentation")
  private String description;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "API resource name or OpenAPI tag name. If empty, automatically determined based on URI", example = "user")
  private String resourceName;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "API resource description", example = "User")
  private String resourceDescription;

}
