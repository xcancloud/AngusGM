package cloud.xcan.angus.core.gm.interfaces.app.facade.dto.open;

import cloud.xcan.angus.api.commonlink.app.AppType;
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

  private Long id;

  private Long appId;

  private String appCode;

  private AppType appType;

  @JsonIgnore
  @Schema(hidden = true)
  private EditionType editionType;

  private String version;

  private String clientId;

  private Long userId;

  protected Long tenantId;

  private Boolean expirationDeleted;

  protected LocalDateTime openDate;

  protected LocalDateTime expirationDate;

  protected LocalDateTime createdDate;

}
