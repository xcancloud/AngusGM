package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import lombok.Data;

import java.util.List;

/**
 * <p>Service detail VO</p>
 */
@Data
public class ServiceDetailVo {

    /**
     * <p>Service name</p>
     */
    private String serviceName;

    /**
     * <p>Display name</p>
     */
    private String displayName;

    /**
     * <p>Total instances count</p>
     */
    private Integer totalInstances;

    /**
     * <p>Up instances count</p>
     */
    private Integer upInstances;

    /**
     * <p>Down instances count</p>
     */
    private Integer downInstances;

    /**
     * <p>Service instances</p>
     */
    private List<ServiceInstanceVo> instances;
}
