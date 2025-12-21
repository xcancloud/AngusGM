package cloud.xcan.angus.core.gm.interfaces.message.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知历史VO
 */
@Data
@Schema(description = "通知历史")
public class NotificationHistoryVo {
    
    @Schema(description = "历史ID", example = "HIST001")
    private String id;
    
    @Schema(description = "标题", example = "用户登录失败告警")
    private String title;
    
    @Schema(description = "渠道名称", example = "System Email")
    private String channel;
    
    @Schema(description = "渠道类型", example = "email")
    private String channelType;
    
    @Schema(description = "接收者", example = "admin@angusgm.com")
    private String recipient;
    
    @Schema(description = "状态", example = "success")
    private String status;
    
    @Schema(description = "发送时间")
    private LocalDateTime sentAt;
    
    @Schema(description = "错误信息")
    private String error;
}
