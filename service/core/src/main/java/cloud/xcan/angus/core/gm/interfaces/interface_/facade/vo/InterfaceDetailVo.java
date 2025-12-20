package cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo;

import cloud.xcan.angus.core.gm.domain.interface_.enums.HttpMethod;
import cloud.xcan.angus.core.gm.domain.interface_.enums.InterfaceStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InterfaceDetailVo {
    private String id;
    private String name;
    private String path;
    private HttpMethod method;
    private String description;
    private Boolean authRequired;
    private InterfaceStatus status;
    private String serviceId;
    private String serviceName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
