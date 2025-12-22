package cloud.xcan.angus.core.gm.interfaces.group.facade.dto;

import cloud.xcan.angus.core.gm.domain.group.enums.GroupStatus;
import cloud.xcan.angus.core.gm.domain.group.enums.GroupType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

/**
 * Create group DTO
 */
@Data
@Schema(description = "创建组请求参数")
public class GroupCreateDto {

  @NotBlank
  @Size(max = 100)
  @Schema(description = "组名称", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Size(max = 50)
  @Schema(description = "组编码", requiredMode = RequiredMode.REQUIRED)
  private String code;

  @Size(max = 500)
  @Schema(description = "描述")
  private String description;

  @Schema(description = "组类型")
  private GroupType type;

  @Schema(description = "负责人ID")
  private Long ownerId;

  @Schema(description = "成员ID列表")
  private List<Long> memberIds;

  @Schema(description = "标签列表")
  private List<String> tags;

  @Schema(description = "状态")
  private GroupStatus status;
}
