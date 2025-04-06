package cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func;

import static cloud.xcan.angus.api.commonlink.AASConstant.MAX_APP_FUNC_SHORT_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_ROOT_PID;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.commonlink.app.func.AppFuncType;
import cloud.xcan.angus.remote.AbstractQuery;
import cloud.xcan.angus.remote.OrderSort;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class AppFuncFindDto extends AbstractQuery {

  private Long id;

  @Length(max = MAX_CODE_LENGTH)
  private String code;

  @Length(max = MAX_NAME_LENGTH)
  private String name;

  @Length(max = MAX_APP_FUNC_SHORT_NAME_LENGTH)
  private String showName;

  @Min(DEFAULT_ROOT_PID)
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
