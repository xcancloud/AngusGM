package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ApiMonitoringFindDto implements Serializable {
    private String name;
    private Boolean enabled;
}
