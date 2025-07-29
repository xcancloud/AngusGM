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

  @Schema(description = "Service ID for updating existing service. Leave empty to create new service")
  private Long id;

  @NotEmpty
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "The display name of the service", example = "User Management Service",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String name;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Unique identifier code for the service. Cannot be modified after creation", example = "USER_MGMT")
  private String code;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "Detailed description of the service functionality and purpose", 
    example = "Comprehensive user management service providing tenant, organization, and user administration capabilities")
  private String description;

  @Schema(description = "Source of the service (e.g., internal, external, third-party)")
  private ServiceSource source;

  @Schema(description = "Whether the service is enabled and available for use. Cannot be modified after creation", defaultValue = "true")
  private Boolean enabled;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "API gateway route path for accessing this service", example = "/user-mgmt")
  private String routePath;

  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "Base URL for accessing the service endpoints", example = "https://api.example.com:8080")
  private String url;

  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "Health check endpoint URL for monitoring service status", example = "https://api.example.com:8080/actuator/health")
  private String healthUrl;

  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "OpenAPI documentation URL for the service API specification", example = "https://api.example.com:8080/v3/api-docs?group=UserAPI")
  private String apiDocUrl;

}
