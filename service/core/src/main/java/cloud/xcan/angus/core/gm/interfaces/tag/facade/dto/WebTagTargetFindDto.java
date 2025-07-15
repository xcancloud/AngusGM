package cloud.xcan.angus.core.gm.interfaces.tag.facade.dto;


import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class WebTagTargetFindDto extends PageQuery {

  //@NotNull -> Required -> The value may be in the filter
  @Schema(description = "Web application tag id", requiredMode = RequiredMode.REQUIRED)
  private Long tagId;

  //@NotNull -> Required -> The value may be in the filter
  @Schema(description = "Web application resource type", requiredMode = RequiredMode.REQUIRED)
  private OrgTargetType targetType;

  @Schema(description = "Web application resource id")
  private Long targetId;

  @Schema(description = "Web application resource name")
  private String targetName;

  private Long createdBy;

  private LocalDateTime createdDate;

}
