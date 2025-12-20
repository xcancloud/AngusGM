package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class MonitoringCreateDto implements Serializable {
    private String name;
    private String description;
    private Boolean enabled = true;
}
