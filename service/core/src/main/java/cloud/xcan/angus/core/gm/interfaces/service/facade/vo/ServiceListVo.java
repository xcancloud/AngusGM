package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import cloud.xcan.angus.core.gm.domain.service.enums.ServiceProtocol;
import cloud.xcan.angus.core.gm.domain.service.enums.ServiceStatus;
import lombok.Data;

/**
 * 服务列表VO
 */
@Data
public class ServiceListVo {

    private String id;

    private String name;

    private String code;

    private ServiceStatus status;

    private ServiceProtocol protocol;

    private String version;

    private String applicationId;

    private String applicationName;

    private Integer interfaceCount;
}
