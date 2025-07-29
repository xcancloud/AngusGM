package cloud.xcan.angus.core.gm.interfaces.app.facade.dto.open;

import cloud.xcan.angus.api.enums.AppType;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.remote.PageQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AppOpenFindDto extends PageQuery {

  @Schema(description = "Application open record identifier")
  private Long id;

  @Schema(description = "Application identifier for filtering")
  private Long appId;

  @Schema(description = "Application code for filtering")
  private String appCode;

  @Schema(description = "Application type for filtering")
  private AppType appType;

  @JsonIgnore
  @Schema(hidden = true)
  private EditionType editionType;

  @Schema(description = "Application version for filtering")
  private String version;

  @Schema(description = "OAuth2 client identifier for filtering")
  private String clientId;

  @Schema(description = "User identifier for filtering")
  private Long userId;

  @Schema(description = "Tenant identifier for filtering")
  protected Long tenantId;

  @Schema(description = "Expiration deletion status for filtering")
  private Boolean expirationDeleted;

  @Schema(description = "Application open date for filtering")
  protected LocalDateTime openDate;

  @Schema(description = "Application expiration date for filtering")
  protected LocalDateTime expirationDate;

  @Schema(description = "Record creation date for filtering")
  protected LocalDateTime createdDate;

}
