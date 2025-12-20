package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.policy.Policy;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyCreateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyDetailVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyListVo;
import org.springframework.stereotype.Component;

@Component
public class PolicyAssembler {
    public Policy toEntity(PolicyCreateDto dto) {
        Policy policy = new Policy();
        policy.setName(dto.getName());
        policy.setEffect(dto.getEffect());
        policy.setResourceIds(dto.getResourceIds());
        policy.setPriority(dto.getPriority());
        policy.setDescription(dto.getDescription());
        return policy;
    }
    
    public Policy toEntity(PolicyUpdateDto dto) {
        Policy policy = new Policy();
        policy.setId(dto.getId());
        policy.setName(dto.getName());
        policy.setEffect(dto.getEffect());
        policy.setResourceIds(dto.getResourceIds());
        policy.setPriority(dto.getPriority());
        policy.setDescription(dto.getDescription());
        return policy;
    }
    
    public PolicyDetailVo toDetailVo(Policy policy) {
        PolicyDetailVo vo = new PolicyDetailVo();
        vo.setId(policy.getId());
        vo.setName(policy.getName());
        vo.setEffect(policy.getEffect());
        vo.setStatus(policy.getStatus());
        vo.setResourceIds(policy.getResourceIds());
        vo.setPriority(policy.getPriority());
        vo.setDescription(policy.getDescription());
        vo.setCreatedAt(policy.getCreatedAt());
        vo.setUpdatedAt(policy.getUpdatedAt());
        return vo;
    }
    
    public PolicyListVo toListVo(Policy policy) {
        PolicyListVo vo = new PolicyListVo();
        vo.setId(policy.getId());
        vo.setName(policy.getName());
        vo.setEffect(policy.getEffect());
        vo.setStatus(policy.getStatus());
        vo.setPriority(policy.getPriority());
        vo.setResourceCount(policy.getResourceIds() != null ? policy.getResourceIds().size() : 0);
        return vo;
    }
}
