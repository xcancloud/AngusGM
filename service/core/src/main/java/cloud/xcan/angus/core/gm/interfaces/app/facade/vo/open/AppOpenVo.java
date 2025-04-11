package cloud.xcan.angus.core.gm.interfaces.app.facade.vo.open;

import cloud.xcan.angus.api.commonlink.app.AppType;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.tag.AppTagInfoVo;
import cloud.xcan.angus.remote.NameJoinField;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AppOpenVo {

  private Long id;

  private Long appId;

  private String appCode;

  private String appName;

  private String appShowName;

  private String appIcon;

  private AppType appType;

  private EditionType editionType;

  private String appUrl;

  private Integer appSequence;

  private String appVersion;

  private List<AppTagInfoVo> appTags;

  @Schema(description = "Open application user id.")
  private Long userId;

  @NameJoinField(id = "userId", repository = "commonUserBaseRepo")
  @Schema(description = "Open application user name.")
  private String userName;

  @Schema(description = "Open application tenant id.")
  protected Long tenantId;

  @NameJoinField(id = "tenantId", repository = "commonTenantRepo")
  @Schema(description = "Open application tenant name.")
  protected String tenantName;

  @Schema(description = "Open expiration deleted flag.")
  private Boolean expirationDeleted;

  @Schema(description = "Operation client open flag.")
  private Boolean opClientOpen;

  @Schema(description = "Open application date.")
  protected LocalDateTime openDate;

  @Schema(description = "Open expiration date.")
  protected LocalDateTime expirationDate;

  @Schema(description = "Open creation date.")
  protected LocalDateTime createdDate;

}
