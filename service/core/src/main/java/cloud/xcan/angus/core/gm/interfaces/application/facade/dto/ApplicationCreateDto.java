package cloud.xcan.angus.core.gm.interfaces.application.facade.dto;

import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationStatus;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

/**
 * <p>
 * Application Create DTO
 * </p>
 */
@Data
@Schema(description = "创建应用请求参数")
public class ApplicationCreateDto {

    @NotBlank
    @Schema(description = "应用编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @NotBlank
    @Schema(description = "应用名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank
    @Schema(description = "显示名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String displayName;

    @NotNull
    @Schema(description = "应用类型", requiredMode = Schema.RequiredMode.REQUIRED)
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
