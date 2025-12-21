package cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler;

import static cloud.xcan.angus.spec.BizConstant.nullSafe;

import cloud.xcan.angus.core.gm.domain.user.User;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserCreateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserFindDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserDetailVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserListVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

/**
 * User assembler for DTO/Domain/VO conversion
 */
public class UserAssembler {

  /**
   * Convert CreateDto to Domain
   */
  public static User toCreateDomain(UserCreateDto dto) {
    User user = new User();
    user.setUsername(dto.getUsername());
    user.setName(dto.getName());
    user.setEmail(dto.getEmail());
    user.setPhone(dto.getPhone());
    user.setPassword(dto.getPassword()); // Should be encrypted in service layer
    user.setAvatar(dto.getAvatar());
    user.setDepartmentId(dto.getDepartmentId());
    user.setRoleIds(dto.getRoleIds());
    user.setStatus(dto.getStatus());
    user.setIsLocked(false);
    return user;
  }

  /**
   * Convert UpdateDto to Domain
   */
  public static User toUpdateDomain(Long id, UserUpdateDto dto) {
    User user = new User();
    user.setId(id);
    user.setName(dto.getName());
    user.setEmail(dto.getEmail());
    user.setPhone(dto.getPhone());
    user.setAvatar(dto.getAvatar());
    user.setDepartmentId(dto.getDepartmentId());
    user.setRoleIds(dto.getRoleIds());
    user.setStatus(dto.getStatus());
    user.setEnableStatus(dto.getEnableStatus());
    return user;
  }

  /**
   * Convert Domain to DetailVo
   */
  public static UserDetailVo toDetailVo(User user) {
    UserDetailVo vo = new UserDetailVo();
    vo.setId(user.getId());
    vo.setUsername(user.getUsername());
    vo.setName(user.getName());
    vo.setEmail(user.getEmail());
    vo.setPhone(user.getPhone());
    vo.setAvatar(user.getAvatar());
    vo.setRole(user.getRole());
    vo.setRoleIds(user.getRoleIds());
    vo.setDepartment(user.getDepartment());
    vo.setDepartmentId(user.getDepartmentId());
    vo.setStatus(user.getStatus());
    vo.setEnableStatus(user.getEnableStatus());
    vo.setAccountType(user.getAccountType());
    vo.setIsLocked(nullSafe(user.getIsLocked(), false));
    vo.setIsOnline(nullSafe(user.getIsOnline(), false));
    vo.setLastLogin(user.getLastLogin());

    // Set auditing fields
    vo.setTenantId(user.getTenantId());
    vo.setCreatedBy(user.getCreatedBy());
    vo.setCreator(user.getCreatedByName());
    vo.setCreatedDate(user.getCreatedDate());
    vo.setModifiedBy(user.getModifiedBy());
    vo.setModifier(user.getModifiedByName());
    vo.setModifiedDate(user.getModifiedDate());

    return vo;
  }

  /**
   * Convert Domain to ListVo
   */
  public static UserListVo toListVo(User user) {
    UserListVo vo = new UserListVo();
    vo.setId(user.getId());
    vo.setUsername(user.getUsername());
    vo.setName(user.getName());
    vo.setEmail(user.getEmail());
    vo.setPhone(user.getPhone());
    vo.setAvatar(user.getAvatar());
    vo.setRole(user.getRole());
    vo.setRoleIds(user.getRoleIds());
    vo.setDepartment(user.getDepartment());
    vo.setDepartmentId(user.getDepartmentId());
    vo.setStatus(user.getStatus());
    vo.setEnableStatus(user.getEnableStatus());
    vo.setAccountType(user.getAccountType());
    vo.setIsLocked(nullSafe(user.getIsLocked(), false));
    vo.setIsOnline(nullSafe(user.getIsOnline(), false));
    vo.setLastLogin(user.getLastLogin());

    // Set auditing fields
    vo.setTenantId(user.getTenantId());
    vo.setCreatedBy(user.getCreatedBy());
    vo.setCreator(user.getCreatedByName());
    vo.setCreatedDate(user.getCreatedDate());
    vo.setModifiedBy(user.getModifiedBy());
    vo.setModifier(user.getModifiedByName());
    vo.setModifiedDate(user.getModifiedDate());

    return vo;
  }

  /**
   * Build query specification from FindDto
   */
  public static GenericSpecification<User> getSpecification(UserFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate", "lastLogin")
        .orderByFields("id", "createdDate", "modifiedDate", "name", "lastLogin")
        .matchSearchFields("name", "username", "email", "phone")
        .build();
    return new GenericSpecification<>(filters);
  }
}
