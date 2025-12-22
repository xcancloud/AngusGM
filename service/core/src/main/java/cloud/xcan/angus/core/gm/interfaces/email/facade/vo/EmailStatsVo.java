package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "邮件统计数据VO")
public class EmailStatsVo {
    
    @Schema(description = "总发送数")
    private Long totalSent;
    
    @Schema(description = "成功数量")
    private Long successCount;
    
    @Schema(description = "失败数量")
    private Long failedCount;
    
    @Schema(description = "今日发送数")
    private Long todaySent;
    
    @Schema(description = "本月发送数")
    private Long thisMonthSent;
    
    @Schema(description = "打开率")
    private Double openRate;
    
    @Schema(description = "点击率")
    private Double clickRate;
}
