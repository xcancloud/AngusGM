package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal;

import static cloud.xcan.angus.spec.BizConstant.buildVoPageResult;
import static cloud.xcan.angus.spec.BizConstant.getMatchSearchFields;

import cloud.xcan.angus.core.gm.application.cmd.policy.PolicyCmd;
import cloud.xcan.angus.core.gm.application.query.policy.PolicyQuery;
import cloud.xcan.angus.core.gm.domain.policy.Policy;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.PolicyFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyCreateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyDefaultDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyPermissionUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.PolicyAssembler;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AvailablePermissionVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyDefaultVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyDetailVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyListVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyPermissionVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyStatsVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyUserVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Implementation of policy (role) facade
 */
@Service
public class PolicyFacadeImpl implements PolicyFacade {

  @Resource
  private PolicyCmd policyCmd;

  @Resource
  private PolicyQuery policyQuery;

  @Override
  public PolicyDetailVo create(PolicyCreateDto dto) {
    Policy policy = PolicyAssembler.toCreateDomain(dto);
    Policy saved = policyCmd.create(policy);
    return PolicyAssembler.toDetailVo(saved);
  }

  @Override
  public PolicyDetailVo update(Long id, PolicyUpdateDto dto) {
    Policy policy = PolicyAssembler.toUpdateDomain(id, dto);
    Policy saved = policyCmd.update(policy);
    return PolicyAssembler.toDetailVo(saved);
  }

  @Override
  public void delete(Long id) {
    policyCmd.delete(id);
  }

  @Override
  public PolicyDetailVo getDetail(Long id) {
    Policy policy = policyQuery.findAndCheck(id);
    return PolicyAssembler.toDetailVo(policy);
  }

  @Override
  public PageResult<PolicyListVo> list(PolicyFindDto dto) {
    GenericSpecification<Policy> spec = PolicyAssembler.getSpecification(dto);
    Page<Policy> page = policyQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, PolicyAssembler::toListVo);
  }

  @Override
  public PolicyStatsVo getStats() {
    PolicyStatsVo stats = new PolicyStatsVo();
    stats.setTotalRoles(policyQuery.countTotal());
    stats.setSystemRoles(policyQuery.countSystemRoles());
    stats.setCustomRoles(policyQuery.countCustomRoles());
    stats.setTotalUsers(policyQuery.countTotalUsers());
    return stats;
  }

  @Override
  public PolicyPermissionVo getPermissions(Long id) {
    Policy policy = policyQuery.findAndCheck(id);
    return PolicyAssembler.toPermissionVo(policy);
  }

  @Override
  public PolicyPermissionVo updatePermissions(Long id, PolicyPermissionUpdateDto dto) {
    Policy policy = policyCmd.updatePermissions(id, PolicyAssembler.toPermissionsDomain(dto));
    return PolicyAssembler.toPermissionVo(policy);
  }

  @Override
  public PageResult<PolicyUserVo> getUsers(Long id, PolicyUserFindDto dto) {
    // TODO: Implement user query for role
    return PageResult.of(new ArrayList<>(), 0L);
  }

  @Override
  public PolicyDefaultVo setDefault(Long id, PolicyDefaultDto dto) {
    Policy policy = policyCmd.setDefault(id, dto.getIsDefault());
    return PolicyAssembler.toDefaultVo(policy);
  }

  @Override
  public List<AvailablePermissionVo> getAvailablePermissions(String appId) {
    // TODO: Implement available permissions query
    return new ArrayList<>();
  }
}
