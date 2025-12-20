package cloud.xcan.angus.core.gm.interfaces.interface_.facade.dto;

import cloud.xcan.angus.core.gm.domain.interface_.enums.HttpMethod;
import cloud.xcan.angus.core.gm.domain.interface_.enums.InterfaceStatus;
import lombok.Data;

@Data
public class InterfaceFindDto {
    private Integer page = 0;
    private Integer size = 20;
    private InterfaceStatus status;
    private HttpMethod method;
    private String serviceId;
    private Boolean authRequired;
}
