package cloud.xcan.angus.core.gm.interfaces.group.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_RELATION_QUOTA;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_REMARK_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class GroupUpdateDto implements Serializable {

  @NotNull
  @Schema(description = "Group identifier to update", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Display name for the group", example = "Product Development Team", maxLength = MAX_NAME_LENGTH)
  private String name;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Unique code identifier for the group", example = "PRODUCT_DEV_001", maxLength = MAX_CODE_LENGTH)
  private String code;

  @Length(max = MAX_REMARK_LENGTH)
  @Schema(description = "Additional description or notes about the group", example = "This group is responsible for product development and innovation",
      maxLength = MAX_REMARK_LENGTH)
  private String remark;

  @Size(max = MAX_RELATION_QUOTA)
  @Schema(description = "Associated tag identifiers for group categorization. Maximum 2000 tags")
  private List<Long> tagIds;
}
