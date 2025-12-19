package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import lombok.Data;

import java.util.Map;

/**
 * 服务统计VO
 */
@Data
public class ServiceStatsVo {

    private Long total;

    private Long enabled;

    private Long disabled;

    private Map<String, Long> protocolDistribution;

    private Long versionCount;

    private Double avgInterfaceCount;
}
