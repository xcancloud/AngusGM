package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import lombok.Data;

import java.util.Map;

@Data
public class EmailStatsVo {
    private Long total;
    private Map<String, Long> byStatus;
    private Map<String, Long> byType;
}
