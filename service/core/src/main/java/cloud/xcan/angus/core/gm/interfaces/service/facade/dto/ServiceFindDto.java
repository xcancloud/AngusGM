package cloud.xcan.angus.core.gm.interfaces.service.facade.dto;

import cloud.xcan.angus.core.gm.domain.service.enums.ServiceProtocol;
import cloud.xcan.angus.core.gm.domain.service.enums.ServiceStatus;
import lombok.Data;

/**
 * 服务查询DTO
 */
@Data
public class ServiceFindDto {

    private ServiceStatus status;

    private ServiceProtocol protocol;

    private String applicationId;

    private String version;

    private Integer page = 0;

    private Integer size = 20;
}
