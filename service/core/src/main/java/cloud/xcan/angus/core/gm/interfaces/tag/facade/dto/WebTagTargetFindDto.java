package cloud.xcan.angus.core.gm.interfaces.tag.facade.dto;


import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class WebTagTargetFindDto extends PageQuery {

  //@NotNull -> Required -> The value may be in the filter
  @Schema(description = "Web application tag identifier for filtering relationships", requiredMode = RequiredMode.REQUIRED)
  private Long tagId;

  //@NotNull -> Required -> The value may be in the filter
  @Schema(description = "Web application resource type for filtering", requiredMode = RequiredMode.REQUIRED)
  private OrgTargetType targetType;

  @Schema(description = "Web application resource identifier for filtering")
  private Long targetId;

  @Schema(description = "Web application resource display name for searching")
  private String targetName;

}
