package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo;

import lombok.Data;

@Data
public class PolicyStatsVo {
    private Long total;
    private Long enabled;
    private Long disabled;
    private Long allowCount;
    private Long denyCount;
}
