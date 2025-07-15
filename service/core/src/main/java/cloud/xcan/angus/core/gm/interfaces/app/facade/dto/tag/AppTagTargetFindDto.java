package cloud.xcan.angus.core.gm.interfaces.app.facade.dto.tag;


import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.commonlink.app.tag.WebTagTargetType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;


@Setter
@Getter
@Accessors(chain = true)
public class AppTagTargetFindDto extends PageQuery {

  //@NotNull -> Required -> The value may be in the filter
  @Schema(description = "Application tag id. It is required. Note: Value may be in the filter",
      requiredMode = RequiredMode.REQUIRED)
  private Long tagId;

  //@NotNull -> Required -> The value may be in the filter
  @Schema(description = "Application tag associated target type (APP/MENU/BUTTON/PANEL). "
      + "It is required. Note: Value may be in the filter", requiredMode = RequiredMode.REQUIRED)
  private WebTagTargetType targetType;

  @Schema(description = "Application tag associated target id")
  private Long targetId;

  @Schema(description = "Application tag associated target name", maxLength = MAX_NAME_LENGTH)
  private String targetName;

  @Schema(description = "The user id of association application tag")
  private Long createdBy;

  @DateTimeFormat(pattern = DATE_FMT)
  @Schema(description = "The date of association application tag")
  private LocalDateTime createdDate;

}
