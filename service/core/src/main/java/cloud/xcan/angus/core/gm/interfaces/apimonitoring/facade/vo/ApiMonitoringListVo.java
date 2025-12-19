package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class ApiMonitoringListVo implements Serializable {
    private Long id;
    private String name;
    private Boolean enabled;
}
