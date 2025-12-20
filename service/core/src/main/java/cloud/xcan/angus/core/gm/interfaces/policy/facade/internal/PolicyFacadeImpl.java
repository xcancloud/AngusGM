package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal;

import cloud.xcan.angus.common.exception.ResourceNotFound;
import cloud.xcan.angus.core.gm.application.cmd.policy.PolicyCmd;
import cloud.xcan.angus.core.gm.application.query.policy.PolicyQuery;
import cloud.xcan.angus.core.gm.domain.policy.Policy;
import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyEffect;
import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyStatus;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.PolicyFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyCreateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.PolicyAssembler;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyDetailVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyListVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyStatsVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PolicyFacadeImpl implements PolicyFacade {
    private final PolicyCmd policyCmd;
    private final PolicyQuery policyQuery;
    private final PolicyAssembler policyAssembler;

    @Override
    public PolicyDetailVo create(PolicyCreateDto dto) {
        Policy policy = policyAssembler.toEntity(dto);
        Policy created = policyCmd.create(policy);
        return policyAssembler.toDetailVo(created);
    }

    @Override
    public PolicyDetailVo update(PolicyUpdateDto dto) {
        Policy policy = policyAssembler.toEntity(dto);
        Policy updated = policyCmd.update(policy);
        return policyAssembler.toDetailVo(updated);
    }

    @Override
    public void enable(Long id) {
        policyCmd.enable(id);
    }

    @Override
    public void disable(Long id) {
        policyCmd.disable(id);
    }

    @Override
    public void delete(Long id) {
        policyCmd.delete(id);
    }

    @Override
    public PolicyDetailVo getById(Long id) {
        Policy policy = policyQuery.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Policy not found"));
        return policyAssembler.toDetailVo(policy);
    }

    @Override
    public Page<PolicyListVo> find(PolicyFindDto dto) {
        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize());
        return policyQuery.find(dto.getStatus(), dto.getEffect(), dto.getPriority(), pageRequest)
                .map(policyAssembler::toListVo);
    }

    @Override
    public PolicyStatsVo getStats() {
        PolicyStatsVo stats = new PolicyStatsVo();
        stats.setTotal(policyQuery.countTotal());
        stats.setEnabled(policyQuery.countByStatus(PolicyStatus.ENABLED));
        stats.setDisabled(policyQuery.countByStatus(PolicyStatus.DISABLED));
        stats.setAllowCount(policyQuery.countByEffect(PolicyEffect.ALLOW));
        stats.setDenyCount(policyQuery.countByEffect(PolicyEffect.DENY));
        return stats;
    }
}
