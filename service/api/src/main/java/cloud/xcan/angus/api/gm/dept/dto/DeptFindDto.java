package cloud.xcan.angus.api.gm.dept.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_ROOT_PID;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@Accessors(chain = true)
public class DeptFindDto extends PageQuery {

  @Schema(description = "Department id.")
  private Long id;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Department code.")
  private String code;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Department name.")
  private String name;

  @Range(min = DEFAULT_ROOT_PID)
  @Schema(description = "Parent department id.")
  private Long pid;

  @Schema(description = "Department level.")
  private Integer level;

  @Schema(description = "Department association tag id.")
  private Long tagId;

  @Schema(description = "Department creation user id.")
  private Long createdBy;

  @Schema(description = "Department creation date.")
  private LocalDateTime createdDate;

}
