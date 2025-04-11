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
  @Schema(description = "Api service id. Note: Modification is not allowed.", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long serviceId;

  @NotEmpty
  @Length(max = MAX_OPENAPI_SUMMARY_LENGTH)
  @Schema(description = "Api summary or name.", example = "Add user api",
      maxLength = MAX_OPENAPI_SUMMARY_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotEmpty
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Api code or OAS operation id.", example = "user:add",
      maxLength = MAX_CODE_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String operationId;

  @NotEmpty
  @Length(max = MAX_URI_LENGTH)
  @Schema(description = "Api http URI.", example = "/api/v1/user",
      maxLength = MAX_URI_LENGTH_X4, requiredMode = RequiredMode.REQUIRED)
  private String uri;

  @NotNull
  @Schema(description = "Api http request method.", example = "POST", requiredMode = RequiredMode.REQUIRED)
  private HttpMethod method;

  @Schema(description = "Angus platform API type. If the value is empty, automatically determine based on URI.", example = "API")
  private ApiType type;

  @Length(max = MAX_OPENAPI_DOC_DESC_LENGTH)
  @Schema(description = "API detailed description.", maxLength = MAX_OPENAPI_DOC_DESC_LENGTH)
  private String description;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Api resource name or OAS tag name. If the value is empty, automatically determine based on URI.", example = "user")
  private String resourceName;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "Api resource description.", example = "User", maxLength = MAX_DESC_LENGTH)
  private String resourceDescription;

}
