package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo;

import lombok.Data;

@Data
public class SmsStatsVo {
    private Long totalSent;
    private Long successCount;
    private Long failedCount;
    private Long todaySent;
    private Long thisMonthSent;
    private Long balance;
}
