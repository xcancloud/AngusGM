package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class MonitoringStatsVo implements Serializable {
    private Long total;
    private Long enabled;
    private Long disabled;
}
