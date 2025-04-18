package cloud.xcan.angus.core.gm.interfaces.service.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH_X2;

import cloud.xcan.angus.api.commonlink.service.ServiceSource;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class ServiceReplaceDto {

  @Schema(description = "Service id. The ID is required when modifying an existing "
      + "service, create a new service when the value is empty.", example = "1")
  private Long id;

  @NotEmpty
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Service name.", example = "User center service",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String name;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Service code. Note: Modification not allowed.", example = "ANGUSGM",
      maxLength = MAX_CODE_LENGTH)
  private String code;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "Service description.", example = "User center service, providing complete tenant, organization, and user management service capabilities, etc.",
      maxLength = MAX_DESC_LENGTH)
  private String description;

  @Schema(description = "Service source.")
  private ServiceSource source;

  @Schema(description = "Whether or not enabled service flag. Note: Modification not allowed.", defaultValue = "true")
  private Boolean enabled;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "The route path in api gateway of service.", example = "/uc",
      maxLength = MAX_CODE_LENGTH)
  private String routePath;

  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "The access url of service.", example = "http://local-api.xcan.cloud:1806",
      maxLength = MAX_URL_LENGTH_X2)
  private String url;

  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "The health checking url of service.", example = "http://local-api.xcan.cloud:1806/actuator/health",
      maxLength = MAX_URL_LENGTH_X2)
  private String healthUrl;

  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "The OpenAPI doc url of service.", example = "http://local-api.xcan.cloud:1806/v3/api-docs?group=Api",
      maxLength = MAX_URL_LENGTH_X2)
  private String apiDocUrl;

}
