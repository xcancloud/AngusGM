package cloud.xcan.angus.core.gm.interfaces.policy.facade.dto;

import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyEffect;
import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyStatus;
import lombok.Data;

@Data
public class PolicyFindDto {
    private PolicyStatus status;
    private PolicyEffect effect;
    private Integer priority;
    private Integer page = 0;
    private Integer size = 20;
}
