package cloud.xcan.angus.core.gm.interfaces.api.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_OPENAPI_DOC_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_OPENAPI_SUMMARY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URI_LENGTH;

import cloud.xcan.angus.api.enums.ApiType;
import cloud.xcan.angus.spec.http.HttpMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class ApiUpdateDto {

  @NotNull
  @Schema(description = "API identifier to update", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  //@Schema(description = "Api service id. Note: Modification is not allowed")
  //private Long serviceId;

  @Length(max = MAX_OPENAPI_SUMMARY_LENGTH)
  @Schema(description = "API summary or display name", example = "Add user api")
  private String name;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "API unique code or OpenAPI operation identifier", example = "user:add")
  private String operationId;

  @Length(max = MAX_URI_LENGTH)
  @Schema(description = "API HTTP endpoint URI", example = "/api/v1/user")
  private String uri;

  @Schema(description = "API HTTP request method", example = "POST")
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
