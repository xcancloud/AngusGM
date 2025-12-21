package cloud.xcan.angus.core.gm.interfaces.application.facade.dto;

import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationStatus;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * Application Find DTO
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询应用请求参数")
public class ApplicationFindDto extends PageQuery {

    @Schema(description = "搜索关键词（名称、编码）")
    private String keyword;

    @Schema(description = "应用类型")
    private ApplicationType type;

    @Schema(description = "状态")
    private ApplicationStatus status;

    @Schema(description = "来源（installed、custom）")
    private String source;

    @Schema(description = "标签筛选")
    private List<String> tags;
}
