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

  private Long id;

  private String code;

  private String name;

  private String showName;

  private Long pid;

  private AppFuncType type;

  private Boolean authCtrl;

  private Boolean enabled;

  private Integer sequence;

  @JsonIgnore
  @Schema(hidden = true)
  private Long appId;

  @Schema(description = "Function associated api id")
  private Long apiId;

  @Schema(description = "Function tag id")
  private Long tagId;

  private LocalDateTime createdDate;

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
