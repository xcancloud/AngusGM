package cloud.xcan.angus.core.gm.interfaces.message.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 收藏通知结果VO
 */
@Data
@Schema(description = "收藏通知结果")
public class NotificationStarResultVo {
    
    @Schema(description = "通知ID", example = "NOTIF001")
    private String id;
    
    @Schema(description = "是否收藏", example = "true")
    private Boolean isStarred;
}
