package cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AuthClientFindDto {

  @Schema(description = "Authentication client identifier")
  private String id;

  @Schema(description = "OAuth2 client identifier for filtering")
  private String clientId;

  @Schema(description = "Tenant identifier for filtering")
  private String tenantId;

}
