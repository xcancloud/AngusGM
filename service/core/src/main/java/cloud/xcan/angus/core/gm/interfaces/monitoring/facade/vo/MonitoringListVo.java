package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class MonitoringListVo implements Serializable {
    private Long id;
    private String name;
    private Boolean enabled;
}
