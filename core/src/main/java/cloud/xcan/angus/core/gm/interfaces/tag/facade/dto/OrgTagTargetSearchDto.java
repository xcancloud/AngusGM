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
public class OrgTagTargetSearchDto extends PageQuery {

  //@NotNull -> Required -> The value may be in the filter
  @Schema(description = "Tag id.", requiredMode = RequiredMode.REQUIRED)
  private Long tagId;

  //@NotNull -> Required -> The value may be in the filter
  @Schema(description = "Tag organization type: USER/DEPT/GROUP.", requiredMode = RequiredMode.REQUIRED)
  private OrgTargetType targetType;

  @Schema(description = "Tag organization id.")
  private Long targetId;

  @Schema(description = "Tag organization name.")
  private String targetName;

  private Long targetCreatedBy;

  private LocalDateTime targetCreatedDate;

}

