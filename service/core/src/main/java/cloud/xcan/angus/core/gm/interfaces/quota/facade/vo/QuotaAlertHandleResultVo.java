package cloud.xcan.angus.core.gm.interfaces.quota.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "配额告警处理结果VO")
public class QuotaAlertHandleResultVo implements Serializable {
    
    @Schema(description = "告警ID")
    private String id;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "处理人ID")
    private String handledBy;
    
    @Schema(description = "处理时间")
    private LocalDateTime handledTime;
    
    @Schema(description = "处理备注")
    private String handleNote;
}
