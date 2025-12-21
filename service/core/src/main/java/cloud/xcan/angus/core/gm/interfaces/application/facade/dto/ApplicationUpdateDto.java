package cloud.xcan.angus.core.gm.interfaces.application.facade.dto;

import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationStatus;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * <p>
 * Application Update DTO
 * </p>
 */
@Data
@Schema(description = "更新应用请求参数")
public class ApplicationUpdateDto {

    @Schema(description = "应用名称")
    private String name;

    @Schema(description = "显示名称")
    private String displayName;

    @Schema(description = "应用类型")
    private ApplicationType type;

    @Schema(description = "应用版本")
    private String version;

    @Schema(description = "版本类型")
    private String versionType;

    @Schema(description = "应用URL")
    private String url;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "排序顺序")
    private Integer sortOrder;

    @Schema(description = "状态")
    private ApplicationStatus status;

    @Schema(description = "描述")
    private String description;
}
