package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo;

import cloud.xcan.angus.core.gm.domain.interfaces.enums.HttpMethod;
import cloud.xcan.angus.core.gm.domain.interfaces.enums.InterfaceStatus;
import lombok.Data;

@Data
public class InterfaceListVo {
    private String id;
    private String name;
    private String path;
    private HttpMethod method;
    private Boolean authRequired;
    private InterfaceStatus status;
    private String serviceId;
    private String serviceName;
}
