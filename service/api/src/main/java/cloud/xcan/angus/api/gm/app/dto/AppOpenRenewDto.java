package cloud.xcan.angus.api.gm.app.dto;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.validator.Code;
import cloud.xcan.angus.validator.Version;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AppOpenRenewDto {

  @Code
  @NotEmpty
  @Schema(description = "Unique application code following specific pattern validation. Must match pattern for alphanumeric characters, underscores, hyphens, and dots. Used for application identification",
      pattern = "^[A-Za-z0-9_:\\-.]{1,80}$", requiredMode = RequiredMode.REQUIRED)
  private String appCode;

  @NotNull
  @Schema(description = "Defines the edition type of the application being renewed. Required for proper application configuration and licensing", requiredMode = RequiredMode.REQUIRED)
  private EditionType editionType;

  @Version
  @NotEmpty
  @Schema(description = "Semantic version string following version pattern validation. Supports major.minor.patch format with optional pre-release and build metadata",
      pattern = "^([0-9]+)\\.([0-9]+)\\.([0-9]+)(?:-([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?(?:\\+[0-9A-Za-z-]+)?$",
      requiredMode = RequiredMode.REQUIRED)
  private String version;

  @NotNull
  @Schema(description = "Identifies the tenant for which the application is being renewed. Required for proper tenant association and access control", requiredMode = RequiredMode.REQUIRED)
  private Long tenantId;

  @Schema(description = "Optional user ID associated with the application renewal")
  private Long userId;

  @NotNull
  @Schema(description = "Timestamp when the application renewal request is processed", requiredMode = RequiredMode.REQUIRED)
  private LocalDateTime openDate;

  //Fix:: @Future There is a delay in the store renewal job during the remote call
  @NotNull
  @Schema(description = "Timestamp when the application access expires. Note: Future validation is disabled due to remote call delays in store renewal jobs", requiredMode = RequiredMode.REQUIRED)
  private LocalDateTime expirationDate;

}
