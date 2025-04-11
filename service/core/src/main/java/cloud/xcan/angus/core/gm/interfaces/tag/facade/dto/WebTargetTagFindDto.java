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
public class WebTargetTagFindDto extends PageQuery {

  @JsonIgnore
  //@NotNull -> Required -> Internal parameters
  @Schema(requiredMode = RequiredMode.REQUIRED, hidden = true)
  private Long targetId;

  @JsonIgnore
  //@NotNull -> Required -> Internal parameters
  @Schema(requiredMode = RequiredMode.REQUIRED, hidden = true)
  private OrgTargetType targetType;

  private Long tagId;

  private String tagName;

  private Long createdBy;

  private LocalDateTime createdDate;

}
