package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo;

import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyEffect;
import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyStatus;
import lombok.Data;

@Data
public class PolicyListVo {
    private Long id;
    private String name;
    private PolicyEffect effect;
    private PolicyStatus status;
    private Integer priority;
    private Integer resourceCount;
}
