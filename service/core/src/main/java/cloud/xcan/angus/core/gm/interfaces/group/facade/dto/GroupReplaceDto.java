package cloud.xcan.angus.core.gm.interfaces.group.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_RELATION_QUOTA;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_REMARK_LENGTH;

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


@Getter
@Setter
@Accessors(chain = true)
public class GroupReplaceDto implements Serializable {

  @Schema(description = "Group identifier for updating existing group. Leave empty to create new group")
  private Long id;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Display name for the group", example = "Product Development Team", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Unique code identifier for the group", example = "PRODUCT_DEV_001", requiredMode = RequiredMode.REQUIRED)
  private String code;

  @Length(max = MAX_REMARK_LENGTH)
  @Schema(description = "Additional description or notes about the group", example = "This group is responsible for product development and innovation",
      maxLength = MAX_REMARK_LENGTH)
  private String remark;

  @Size(max = MAX_RELATION_QUOTA)
  @Schema(description = "Associated tag identifiers for group categorization. Maximum 2000 tags")
  private List<Long> tagIds;

}
