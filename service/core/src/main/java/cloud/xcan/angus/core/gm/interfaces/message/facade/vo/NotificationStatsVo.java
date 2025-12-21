package cloud.xcan.angus.core.gm.interfaces.message.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通知统计VO
 */
@Data
@Schema(description = "通知统计")
public class NotificationStatsVo {
    
    @Schema(description = "总通知数", example = "156")
    private Integer totalNotifications;
    
    @Schema(description = "未读通知数", example = "28")
    private Integer unreadNotifications;
    
    @Schema(description = "收藏通知数", example = "12")
    private Integer starredNotifications;
    
    @Schema(description = "已归档通知数", example = "45")
    private Integer archivedNotifications;
}
