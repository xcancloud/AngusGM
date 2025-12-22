package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import lombok.Data;

import java.util.List;

/**
 * <p>Service list VO</p>
 */
@Data
public class ServiceListVo {

    /**
     * <p>Service name</p>
     */
    private String serviceName;

    /**
     * <p>Display name</p>
     */
    private String displayName;

    /**
     * <p>Service instances</p>
     */
    private List<ServiceInstanceVo> instances;
}
