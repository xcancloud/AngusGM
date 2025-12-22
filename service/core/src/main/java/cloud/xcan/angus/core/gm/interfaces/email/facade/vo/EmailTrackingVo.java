package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "邮件跟踪统计VO")
public class EmailTrackingVo {
    
    @Schema(description = "邮件ID")
    private Long emailId;
    
    @Schema(description = "邮件主题")
    private String subject;
    
    @Schema(description = "发送时间")
    private LocalDateTime sentTime;
    
    @Schema(description = "送达时间")
    private LocalDateTime deliveredTime;
    
    @Schema(description = "是否已打开")
    private Boolean opened;
    
    @Schema(description = "打开时间")
    private LocalDateTime openedTime;
    
    @Schema(description = "打开次数")
    private Integer openCount;
    
    @Schema(description = "是否已点击")
    private Boolean clicked;
    
    @Schema(description = "点击次数")
    private Integer clickCount;
    
    @Schema(description = "是否退回")
    private Boolean bounced;
    
    @Schema(description = "是否投诉")
    private Boolean complained;
}
