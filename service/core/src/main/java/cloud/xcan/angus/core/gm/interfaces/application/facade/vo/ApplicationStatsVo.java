package cloud.xcan.angus.core.gm.interfaces.application.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Application Statistics VO
 */
@Data
@Schema(description = "应用统计数据")
public class ApplicationStatsVo {

    @Schema(description = "总应用数")
    private Long totalApplications;

    @Schema(description = "已启用应用数")
    private Long enabledApplications;

    @Schema(description = "已禁用应用数")
    private Long disabledApplications;

    @Schema(description = "基础应用数")
    private Long baseApplications;

    @Schema(description = "业务应用数")
    private Long businessApplications;

    @Schema(description = "系统应用数")
    private Long systemApplications;
}
