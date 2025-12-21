package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import lombok.Data;

@Data
public class EmailStatsVo {
    private Long totalSent;
    private Long successCount;
    private Long failedCount;
    private Long todaySent;
    private Long thisMonthSent;
    private Double openRate;
    private Double clickRate;
}
