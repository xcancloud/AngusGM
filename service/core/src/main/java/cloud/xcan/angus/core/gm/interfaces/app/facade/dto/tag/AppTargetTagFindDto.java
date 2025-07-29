package cloud.xcan.angus.core.gm.interfaces.app.facade.dto.tag;

import cloud.xcan.angus.api.commonlink.app.tag.WebTagTargetType;
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
public class AppTargetTagFindDto extends PageQuery {

  @JsonIgnore
  @Schema(hidden = true)
  private WebTagTargetType targetType;

  @JsonIgnore
  @Schema(hidden = true)
  private Long targetId;

  @Schema(description = "Application tag identifier for filtering")
  private Long tagId;

  @Schema(description = "Application tag display name for filtering")
  private String tagName;

  @Schema(description = "Application tag creator identifier for filtering")
  private Long createdBy;

  @Schema(description = "Application tag creation date for filtering")
  private LocalDateTime createdDate;

}
