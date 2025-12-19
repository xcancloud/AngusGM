package cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func;

import cloud.xcan.angus.api.commonlink.app.func.AppFuncType;
import cloud.xcan.angus.remote.AbstractQuery;
import cloud.xcan.angus.remote.OrderSort;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class AppFuncFindDto extends AbstractQuery {

  @Schema(description = "Application function identifier")
  private Long id;

  @Schema(description = "Application function code for filtering")
  private String code;

  @Schema(description = "Application function display name for filtering")
  private String name;

  @Schema(description = "Application function short display name for filtering")
  private String showName;

  @Schema(description = "Parent function identifier for hierarchical filtering")
  private Long pid;

  @Schema(description = "Application function type for filtering")
  private AppFuncType type;

  @Schema(description = "Authorization control status for filtering")
  private Boolean authCtrl;

  @Schema(description = "Function enabled status for filtering")
  private Boolean enabled;

  @Schema(description = "Function display sequence order for filtering")
  private Integer sequence;

  @JsonIgnore
  @Schema(hidden = true)
  private Long appId;

  @Schema(description = "Associated API identifier for filtering")
  private Long apiId;

  @Schema(description = "Function tag identifier for filtering")
  private Long tagId;

  @Override
  @JsonIgnore
  @Schema(hidden = true)
  public String getDefaultOrderBy() {
    return "sequence";
  }

  @Override
  @JsonIgnore
  @Schema(hidden = true)
  public OrderSort getDefaultOrderSort() {
    return OrderSort.ASC;
  }
}
