package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class MonitoringFindDto implements Serializable {
    private String name;
    private Boolean enabled;
}
