package cloud.xcan.angus.core.gm.interfaces.message.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 批量已读结果VO
 */
@Data
@Schema(description = "批量已读结果")
public class NotificationBatchReadResultVo {
    
    @Schema(description = "标记数量", example = "3")
    private Integer count;
    
    @Schema(description = "已读时间")
    private LocalDateTime readTime;
}
