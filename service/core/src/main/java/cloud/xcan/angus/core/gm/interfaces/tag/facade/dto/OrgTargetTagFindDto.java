package cloud.xcan.angus.core.gm.interfaces.tag.facade.dto;

import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.remote.PageQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class OrgTargetTagFindDto extends PageQuery {

  @JsonIgnore
  //Required -> Internal parameters
  @Schema(description = "Organizational resource identifier (internal parameter)", requiredMode = RequiredMode.REQUIRED, hidden = true)
  private Long targetId;

  @JsonIgnore
  //Required -> Internal parameters
  @Schema(description = "Organizational resource type (internal parameter)", requiredMode = RequiredMode.REQUIRED, hidden = true)
  private OrgTargetType targetType;

  @Schema(description = "Organizational tag identifier for filtering")
  private Long tagId;

  @Schema(description = "Organizational tag display name for searching")
  private String tagName;

  @Schema(description = "User identifier who created the tag relationship")
  private Long createdBy;

  @Schema(description = "Tag relationship creation date for filtering")
  private LocalDateTime createdDate;

}
