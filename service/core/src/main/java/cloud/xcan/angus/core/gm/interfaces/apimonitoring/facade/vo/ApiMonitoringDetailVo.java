package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ApiMonitoringDetailVo implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
