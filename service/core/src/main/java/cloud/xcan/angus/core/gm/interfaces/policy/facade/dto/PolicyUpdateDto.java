package cloud.xcan.angus.core.gm.interfaces.policy.facade.dto;

import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyEffect;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class PolicyUpdateDto {
    @NotNull
    private Long id;
    
    private String name;
    
    private PolicyEffect effect;
    
    private List<Long> resourceIds;
    
    private Integer priority;
    
    private String description;
}
