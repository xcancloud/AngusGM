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

@Getter
@Setter
@Accessors(chain = true)
public class AppOpenDto {

  @Code
  @NotEmpty
  @Schema(description = "Open application code.", pattern = "^[A-Za-z0-9_:\\-.]{1,80}$",
      requiredMode = RequiredMode.REQUIRED)
  private String appCode;

  @NotNull
  @Schema(description = "Open application edition type.", requiredMode = RequiredMode.REQUIRED)
  private EditionType editionType;

  @Version
  @NotEmpty
  @Schema(description = "Open application version.",
      pattern = "^([0-9]+)\\.([0-9]+)\\.([0-9]+)(?:-([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?(?:\\+[0-9A-Za-z-]+)?$",
      requiredMode = RequiredMode.REQUIRED)
  private String version;

  @NotNull
  @Schema(description = "Open tenant id.", requiredMode = RequiredMode.REQUIRED)
  private Long tenantId;

  @Schema(description = "Open user id.")
  private Long userId;

  @NotNull
  @Schema(description = "Open date.", requiredMode = RequiredMode.REQUIRED)
  protected LocalDateTime openDate;

  //Fix:: @Future There is a delay in the store renewal job during the remote call
  @NotNull
  @Schema(description = "Open application expired date.", requiredMode = RequiredMode.REQUIRED)
  protected LocalDateTime expirationDate;

}
