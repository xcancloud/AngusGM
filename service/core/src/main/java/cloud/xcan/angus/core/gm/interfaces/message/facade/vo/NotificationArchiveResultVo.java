package cloud.xcan.angus.core.gm.interfaces.message.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 归档通知结果VO
 */
@Data
@Schema(description = "归档通知结果")
public class NotificationArchiveResultVo {
    
    @Schema(description = "通知ID", example = "NOTIF001")
    private String id;
    
    @Schema(description = "是否归档", example = "true")
    private Boolean isArchived;
}
