package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo;

import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyEffect;
import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PolicyDetailVo {
    private Long id;
    private String name;
    private PolicyEffect effect;
    private PolicyStatus status;
    private List<Long> resourceIds;
    private Integer priority;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
