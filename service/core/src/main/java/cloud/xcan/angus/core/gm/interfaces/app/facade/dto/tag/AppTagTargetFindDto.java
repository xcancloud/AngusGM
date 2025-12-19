package cloud.xcan.angus.core.gm.interfaces.app.facade.dto.tag;


import cloud.xcan.angus.api.commonlink.app.tag.WebTagTargetType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class AppTagTargetFindDto extends PageQuery {

  //@NotNull -> Required -> The value may be in the filter
  @Schema(description = "Application tag identifier. Required field. Note: Value may be used in filtering",
      requiredMode = RequiredMode.REQUIRED)
  private Long tagId;

  //@NotNull -> Required -> The value may be in the filter
  @Schema(description = "Application tag associated target type (APP/MENU/BUTTON/PANEL). Required field. Note: Value may be used in filtering", requiredMode = RequiredMode.REQUIRED)
  private WebTagTargetType targetType;

  @Schema(description = "Application tag associated target identifier")
  private Long targetId;

  @Schema(description = "Application tag associated target display name")
  private String targetName;

}
