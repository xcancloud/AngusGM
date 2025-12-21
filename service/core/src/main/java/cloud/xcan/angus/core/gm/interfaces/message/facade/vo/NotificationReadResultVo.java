package cloud.xcan.angus.core.gm.interfaces.message.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知已读结果VO
 */
@Data
@Schema(description = "通知已读结果")
public class NotificationReadResultVo {
    
    @Schema(description = "通知ID", example = "NOTIF001")
    private String id;
    
    @Schema(description = "是否已读", example = "true")
    private Boolean isRead;
    
    @Schema(description = "已读时间")
    private LocalDateTime readTime;
}
