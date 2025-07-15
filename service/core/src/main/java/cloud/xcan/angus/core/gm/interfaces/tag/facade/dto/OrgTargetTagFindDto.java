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
  @Schema(description = "Tag organization id", requiredMode = RequiredMode.REQUIRED, hidden = true)
  private Long targetId;

  @JsonIgnore
  //Required -> Internal parameters
  @Schema(description = "Tag organization type", requiredMode = RequiredMode.REQUIRED, hidden = true)
  private OrgTargetType targetType;

  private Long tagId;

  private String tagName;

  private Long createdBy;

  private LocalDateTime createdDate;

}
