package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import lombok.Data;

/**
 * <p>Service statistics VO</p>
 */
@Data
public class ServiceStatsVo {

    /**
     * <p>Total services count</p>
     */
    private Long totalServices;

    /**
     * <p>Total instances count</p>
     */
    private Long totalInstances;

    /**
     * <p>Up instances count</p>
     */
    private Long upInstances;

    /**
     * <p>Down instances count</p>
     */
    private Long downInstances;

    /**
     * <p>Average response time</p>
     */
    private Integer avgResponseTime;
}
