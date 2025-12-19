package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo;

import cloud.xcan.angus.core.gm.domain.sms.SmsStatus;
import cloud.xcan.angus.core.gm.domain.sms.SmsType;
import lombok.Data;
import java.util.Map;

@Data
public class SmsStatsVo {
    private Long total;
    private Map<SmsStatus, Long> statusDistribution;
    private Map<SmsType, Long> typeDistribution;
    private Double successRate;
    private Double avgDeliveryTime;
    private Map<String, Long> providerStats;
}
