package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import cloud.xcan.angus.core.gm.domain.service.enums.ServiceProtocol;
import cloud.xcan.angus.core.gm.domain.service.enums.ServiceStatus;
import lombok.Data;

import java.time.Instant;

/**
 * 服务详情VO
 */
@Data
public class ServiceDetailVo {

    private String id;

    private String name;

    private String code;

    private String description;

    private ServiceStatus status;

    private ServiceProtocol protocol;

    private String version;

    private String baseUrl;

    private String applicationId;

    private String applicationName;

    private Integer interfaceCount;

    private Instant createdAt;

    private Instant updatedAt;
}
