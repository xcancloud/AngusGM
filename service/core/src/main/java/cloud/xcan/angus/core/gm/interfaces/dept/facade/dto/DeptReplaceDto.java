package cloud.xcan.angus.core.gm.interfaces.dept.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_ROOT_PID;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_RELATION_QUOTA;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@Accessors(chain = true)
public class DeptReplaceDto implements Serializable {

  @Schema(description = "Department id. The ID is required when modifying an existing department, "
      + "create a new department when the value is empty", example = "1")
  private Long id;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Department name", example = "MarketingDepartment",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Department code", example = "HR", maxLength = MAX_CODE_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String code;

  @NotNull
  @Range(min = DEFAULT_ROOT_PID)
  @Schema(description = "Parent department id", example = "-1", requiredMode = RequiredMode.REQUIRED)
  private Long pid;

  @Size(max = MAX_RELATION_QUOTA)
  @Schema(description = "Department association tag ids, the maximum support is `2000`")
  private List<Long> tagIds;

}
