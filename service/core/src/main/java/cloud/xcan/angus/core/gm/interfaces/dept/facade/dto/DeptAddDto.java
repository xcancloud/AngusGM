package cloud.xcan.angus.core.gm.interfaces.dept.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_ROOT_PID;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_RELATION_QUOTA;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
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
public class DeptAddDto implements Serializable {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Display name for the department", example = "MarketingDepartment", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Unique code for the department", example = "HR", requiredMode = RequiredMode.REQUIRED)
  private String code;

  @Range(min = DEFAULT_ROOT_PID)
  @Schema(description = "Parent department identifier", minimum = "-1")
  private Long pid;

  @Size(max = MAX_RELATION_QUOTA)
  @Schema(description = "Department association tag identifiers")
  private List<Long> tagIds;

}
