package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ApiMonitoringUpdateDto implements Serializable {
    private String name;
    private String description;
}
