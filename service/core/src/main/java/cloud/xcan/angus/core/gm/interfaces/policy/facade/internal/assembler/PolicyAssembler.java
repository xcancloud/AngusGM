package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler;

import static cloud.xcan.angus.spec.BizConstant.nullSafe;

import cloud.xcan.angus.core.gm.domain.policy.Policy;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyCreateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyPermissionUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyDefaultVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyDetailVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyListVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyPermissionVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.remote.search.SearchOperation;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>Policy assembler for DTO/Domain/VO conversion</p>
 */
public class PolicyAssembler {

  /**
   * <p>Convert CreateDto to Domain entity</p>
   */
  public static Policy toCreateDomain(PolicyCreateDto dto, ObjectMapper objectMapper) {
    Policy policy = new Policy();
    policy.setName(dto.getName());
    policy.setCode(dto.getCode());
    policy.setDescription(dto.getDescription());
    policy.setAppId(dto.getAppId());
    policy.setIsDefault(nullSafe(dto.getIsDefault(), false));
    policy.setIsSystem(false);

    // Convert permissions DTO to Domain PermissionInfo
    if (dto.getPermissions() != null && !dto.getPermissions().isEmpty()) {
      List<Policy.PermissionInfo> permissionList = dto.getPermissions().stream()
          .map(p -> {
            Policy.PermissionInfo info = new Policy.PermissionInfo();
            info.setResource(p.getResource());
            info.setActions(p.getActions());
            return info;
          })
          .collect(Collectors.toList());
      policy.setPermissionList(permissionList);
    }

    return policy;
  }

  /**
   * <p>Convert UpdateDto to Domain entity</p>
   */
  public static Policy toUpdateDomain(Long id, PolicyUpdateDto dto, ObjectMapper objectMapper) {
    Policy policy = new Policy();
    policy.setId(id);
    policy.setName(dto.getName());
    policy.setDescription(dto.getDescription());
    policy.setIsDefault(dto.getIsDefault());

    // Convert permissions DTO to Domain PermissionInfo
    if (dto.getPermissions() != null && !dto.getPermissions().isEmpty()) {
      List<Policy.PermissionInfo> permissionList = dto.getPermissions().stream()
          .map(p -> {
            Policy.PermissionInfo info = new Policy.PermissionInfo();
            info.setResource(p.getResource());
            info.setActions(p.getActions());
            return info;
          })
          .collect(Collectors.toList());
      policy.setPermissionList(permissionList);
    }

    return policy;
  }

  /**
   * <p>Convert Domain to DetailVo</p>
   */
  public static PolicyDetailVo toDetailVo(Policy policy) {
    PolicyDetailVo vo = new PolicyDetailVo();
    vo.setId(policy.getId());
    vo.setName(policy.getName());
    vo.setCode(policy.getCode());
    vo.setDescription(policy.getDescription());
    vo.setIsSystem(nullSafe(policy.getIsSystem(), false));
    vo.setIsDefault(nullSafe(policy.getIsDefault(), false));
    vo.setUserCount(nullSafe(policy.getUserCount(), 0L));
    vo.setAppId(policy.getAppId());
    vo.setAppName(policy.getAppName());

    // Set audit fields (inherited from TenantAuditingVo)
    // Note: These fields are automatically set by TenantAuditingVo base class

    // Convert permissions
    if (policy.getPermissionList() != null) {
      List<PolicyDetailVo.PermissionVo> permissionVos = policy.getPermissionList().stream()
          .map(p -> {
            PolicyDetailVo.PermissionVo pvo = new PolicyDetailVo.PermissionVo();
            pvo.setResource(p.getResource());
            pvo.setResourceName(p.getResourceName());
            pvo.setActions(p.getActions());
            return pvo;
          })
          .collect(Collectors.toList());
      vo.setPermissions(permissionVos);
    }

    // Convert users
    if (policy.getUserList() != null) {
      List<PolicyDetailVo.RoleUserVo> userVos = policy.getUserList().stream()
          .map(u -> {
            PolicyDetailVo.RoleUserVo uvo = new PolicyDetailVo.RoleUserVo();
            uvo.setId(u.getId());
            uvo.setName(u.getName());
            uvo.setEmail(u.getEmail());
            uvo.setAvatar(u.getAvatar());
            return uvo;
          })
          .collect(Collectors.toList());
      vo.setUsers(userVos);
    }

    return vo;
  }

  /**
   * <p>Convert Domain to ListVo</p>
   */
  public static PolicyListVo toListVo(Policy policy) {
    PolicyListVo vo = new PolicyListVo();
    vo.setId(policy.getId());
    vo.setName(policy.getName());
    vo.setCode(policy.getCode());
    vo.setDescription(policy.getDescription());
    vo.setIsSystem(nullSafe(policy.getIsSystem(), false));
    vo.setIsDefault(nullSafe(policy.getIsDefault(), false));
    vo.setUserCount(nullSafe(policy.getUserCount(), 0L));
    vo.setAppId(policy.getAppId());
    vo.setAppName(policy.getAppName());

    // Set audit fields (inherited from TenantAuditingVo)
    // Note: These fields are automatically set by TenantAuditingVo base class

    return vo;
  }

  /**
   * <p>Convert Domain to PermissionVo</p>
   */
  public static PolicyPermissionVo toPermissionVo(Policy policy) {
    PolicyPermissionVo vo = new PolicyPermissionVo();
    vo.setRoleId(policy.getId());
    vo.setRoleName(policy.getName());
    vo.setModifiedDate(LocalDateTime.now());

    if (policy.getPermissionList() != null) {
      List<PolicyPermissionVo.PermissionItemVo> permissionVos = policy.getPermissionList().stream()
          .map(p -> {
            PolicyPermissionVo.PermissionItemVo pvo = new PolicyPermissionVo.PermissionItemVo();
            pvo.setResource(p.getResource());
            pvo.setResourceName(p.getResourceName());
            pvo.setActions(p.getActions());
            pvo.setDescription(p.getDescription());
            return pvo;
          })
          .collect(Collectors.toList());
      vo.setPermissions(permissionVos);
    } else {
      vo.setPermissions(new ArrayList<>());
    }

    return vo;
  }

  /**
   * <p>Convert Domain to DefaultVo</p>
   */
  public static PolicyDefaultVo toDefaultVo(Policy policy) {
    PolicyDefaultVo vo = new PolicyDefaultVo();
    vo.setId(policy.getId());
    vo.setName(policy.getName());
    vo.setIsDefault(nullSafe(policy.getIsDefault(), false));
    vo.setModifiedDate(LocalDateTime.now());
    return vo;
  }

  /**
   * <p>Convert PermissionUpdateDto to Domain PermissionInfo list</p>
   */
  public static List<Policy.PermissionInfo> toPermissionsDomain(PolicyPermissionUpdateDto dto) {
    if (dto.getPermissions() == null || dto.getPermissions().isEmpty()) {
      return new ArrayList<>();
    }
    return dto.getPermissions().stream()
        .map(p -> {
          Policy.PermissionInfo info = new Policy.PermissionInfo();
          info.setResource(p.getResource());
          info.setActions(p.getActions());
          return info;
        })
        .collect(Collectors.toList());
  }

  /**
   * <p>Build query specification from FindDto</p>
   */
  public static GenericSpecification<Policy> getSpecification(PolicyFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate", "name")
        .matchSearchFields("name", "code")
        .build();

    // Add keyword filter (search in name and code)
    if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
      filters.add(new SearchCriteria("name", dto.getKeyword(), SearchOperation.MATCH));
      filters.add(new SearchCriteria("code", dto.getKeyword(), SearchOperation.MATCH));
    }

    // Add appId filter
    if (dto.getAppId() != null && !dto.getAppId().isEmpty()) {
      filters.add(new SearchCriteria("appId", dto.getAppId(), SearchOperation.EQUAL));
    }

    // Add isSystem filter
    if (dto.getIsSystem() != null) {
      filters.add(new SearchCriteria("isSystem", dto.getIsSystem(), SearchOperation.EQUAL));
    }

    // Add isDefault filter
    if (dto.getIsDefault() != null) {
      filters.add(new SearchCriteria("isDefault", dto.getIsDefault(), SearchOperation.EQUAL));
    }

    return new GenericSpecification<>(filters);
  }
}
