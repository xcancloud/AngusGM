package cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo;

import lombok.Data;
import java.util.Map;

@Data
public class InterfaceStatsVo {
    private Long total;
    private Long enabled;
    private Long disabled;
    private Map<String, Long> methodDistribution;
    private Long authRequiredCount;
}
