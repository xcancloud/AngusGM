package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal;

import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.gm.application.cmd.policy.PolicyCmd;
import cloud.xcan.angus.core.gm.application.query.policy.PolicyQuery;
import cloud.xcan.angus.core.gm.domain.policy.Policy;
import cloud.xcan.angus.core.gm.domain.user.User;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * <p>Implementation of policy (role) facade</p>
 */
@Service
public class PolicyFacadeImpl implements PolicyFacade {

  @Resource
  private PolicyCmd policyCmd;

  @Resource
  private PolicyQuery policyQuery;

  @Resource
  private ObjectMapper objectMapper;

  @Override
  public PolicyDetailVo create(PolicyCreateDto dto) {
    Policy policy = PolicyAssembler.toCreateDomain(dto, objectMapper);
    Policy saved = policyCmd.create(policy);
    return PolicyAssembler.toDetailVo(saved);
  }

  @Override
  public PolicyDetailVo update(Long id, PolicyUpdateDto dto) {
    Policy policy = PolicyAssembler.toUpdateDomain(id, dto, objectMapper);
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
    // Load permissions from JSON
    loadPermissionsFromJson(policy);
    // Load user count
    policy.setUserCount(policyQuery.countUsersByPolicyId(id));
    return PolicyAssembler.toDetailVo(policy);
  }

  @Override
  public PageResult<PolicyListVo> list(PolicyFindDto dto) {
    GenericSpecification<Policy> spec = PolicyAssembler.getSpecification(dto);
    Page<Policy> page = policyQuery.find(spec, dto.tranPage(),
        false, new String[]{"name", "code"});
    
    // Set user counts for each policy
    if (page.hasContent()) {
      page.getContent().forEach(policy -> {
        policy.setUserCount(policyQuery.countUsersByPolicyId(policy.getId()));
      });
    }
    
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
    loadPermissionsFromJson(policy);
    return PolicyAssembler.toPermissionVo(policy);
  }

  @Override
  public PolicyPermissionVo updatePermissions(Long id, PolicyPermissionUpdateDto dto) {
    Policy policy = policyCmd.updatePermissions(id, PolicyAssembler.toPermissionsDomain(dto));
    loadPermissionsFromJson(policy);
    return PolicyAssembler.toPermissionVo(policy);
  }

  @Override
  public PageResult<PolicyUserVo> getUsers(Long id, PolicyUserFindDto dto) {
    Page<User> userPage = policyQuery.findUsersByPolicyId(id, dto.tranPage());
    
    List<PolicyUserVo> userVos = userPage.getContent().stream()
        .map(user -> {
          PolicyUserVo vo = new PolicyUserVo();
          vo.setId(user.getId());
          vo.setName(user.getName());
          vo.setEmail(user.getEmail());
          vo.setAvatar(user.getAvatar());
          vo.setDepartment(user.getDepartment());
          vo.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
          
          // Set audit fields (inherited from TenantAuditingVo)
          // Note: These fields are automatically set by TenantAuditingVo base class
          
          return vo;
        })
        .collect(Collectors.toList());
    
    return PageResult.of(userPage.getTotalElements(), userVos);
  }

  @Override
  public PolicyDefaultVo setDefault(Long id, PolicyDefaultDto dto) {
    Policy policy = policyCmd.setDefault(id, dto.getIsDefault());
    return PolicyAssembler.toDefaultVo(policy);
  }

  @Override
  public List<AvailablePermissionVo> getAvailablePermissions(String appId) {
    // Define standard resources and actions
    List<AvailablePermissionVo> permissions = new ArrayList<>();
    
    // Users resource
    AvailablePermissionVo usersResource = new AvailablePermissionVo();
    usersResource.setResource("users");
    usersResource.setResourceName("用户管理");
    usersResource.setDescription("用户的增删改查");
    usersResource.setActions(Arrays.asList(
        createAction("create", "创建"),
        createAction("read", "查看"),
        createAction("update", "编辑"),
        createAction("delete", "删除")
    ));
    permissions.add(usersResource);
    
    // Tenants resource
    AvailablePermissionVo tenantsResource = new AvailablePermissionVo();
    tenantsResource.setResource("tenants");
    tenantsResource.setResourceName("租户管理");
    tenantsResource.setDescription("租户的增删改查");
    tenantsResource.setActions(Arrays.asList(
        createAction("create", "创建"),
        createAction("read", "查看"),
        createAction("update", "编辑"),
        createAction("delete", "删除")
    ));
    permissions.add(tenantsResource);
    
    // Departments resource
    AvailablePermissionVo departmentsResource = new AvailablePermissionVo();
    departmentsResource.setResource("departments");
    departmentsResource.setResourceName("部门管理");
    departmentsResource.setDescription("部门的增删改查");
    departmentsResource.setActions(Arrays.asList(
        createAction("create", "创建"),
        createAction("read", "查看"),
        createAction("update", "编辑"),
        createAction("delete", "删除")
    ));
    permissions.add(departmentsResource);
    
    // Roles resource
    AvailablePermissionVo rolesResource = new AvailablePermissionVo();
    rolesResource.setResource("roles");
    rolesResource.setResourceName("角色管理");
    rolesResource.setDescription("角色的增删改查");
    rolesResource.setActions(Arrays.asList(
        createAction("create", "创建"),
        createAction("read", "查看"),
        createAction("update", "编辑"),
        createAction("delete", "删除")
    ));
    permissions.add(rolesResource);
    
    // Filter by appId if provided
    if (appId != null && !appId.isEmpty()) {
      // In a real implementation, you would query available permissions from a configuration
      // or API definition based on appId
      // For now, return all standard permissions
    }
    
    return permissions;
  }

  /**
   * <p>Load permissions from JSON string to PermissionInfo list</p>
   */
  private void loadPermissionsFromJson(Policy policy) {
    if (policy.getPermissions() != null && !policy.getPermissions().isEmpty()) {
      try {
        List<Policy.PermissionInfo> permissionList = objectMapper.readValue(
            policy.getPermissions(),
            new com.fasterxml.jackson.core.type.TypeReference<List<Policy.PermissionInfo>>() {});
        policy.setPermissionList(permissionList);
      } catch (Exception e) {
        policy.setPermissionList(new ArrayList<>());
      }
    } else {
      policy.setPermissionList(new ArrayList<>());
    }
  }

  /**
   * <p>Create action VO</p>
   */
  private AvailablePermissionVo.ActionVo createAction(String action, String name) {
    AvailablePermissionVo.ActionVo actionVo = new AvailablePermissionVo.ActionVo();
    actionVo.setAction(action);
    actionVo.setName(name);
    return actionVo;
  }
}
