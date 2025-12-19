package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class ApiMonitoringStatsVo implements Serializable {
    private Long total;
    private Long enabled;
    private Long disabled;
}
