package cloud.xcan.angus.core.gm.interfaces.department.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.department.Department;
import cloud.xcan.angus.core.gm.domain.user.User;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentCreateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentFindDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentMemberFindDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentListVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentMemberVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.remote.search.SearchOperation;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Department assembler for DTO/Domain/VO conversion
 */
public class DepartmentAssembler {

  /**
   * Convert CreateDto to Domain
   */
  public static Department toCreateDomain(DepartmentCreateDto dto) {
    Department department = new Department();
    department.setName(dto.getName());
    department.setCode(dto.getCode());
    department.setParentId(dto.getParentId());
    department.setLeaderId(dto.getManagerId()); // Map managerId to leaderId in entity
    department.setDescription(dto.getDescription());
    department.setSortOrder(dto.getSortOrder());
    department.setStatus(dto.getStatus());
    return department;
  }

  /**
   * Convert UpdateDto to Domain
   */
  public static Department toUpdateDomain(Long id, DepartmentUpdateDto dto) {
    Department department = new Department();
    department.setId(id);
    department.setName(dto.getName());
    if (dto.getCode() != null) {
      department.setCode(dto.getCode());
    }
    department.setParentId(dto.getParentId());
    department.setLeaderId(dto.getManagerId()); // Map managerId to leaderId in entity
    department.setDescription(dto.getDescription());
    department.setSortOrder(dto.getSortOrder());
    department.setStatus(dto.getStatus());
    return department;
  }

  /**
   * Convert Domain to DetailVo
   */
  public static DepartmentDetailVo toDetailVo(Department department) {
    DepartmentDetailVo vo = new DepartmentDetailVo();
    vo.setId(department.getId());
    vo.setName(department.getName());
    vo.setCode(department.getCode());
    vo.setParentId(department.getParentId());
    vo.setParentName(department.getParentName());
    vo.setLevel(department.getLevel());
    vo.setSortOrder(department.getSortOrder());
    vo.setManagerId(department.getLeaderId()); // Map leaderId to managerId in VO
    vo.setManagerName(department.getManagerName());
    vo.setManagerAvatar(department.getManagerAvatar());
    vo.setDescription(department.getDescription());
    vo.setStatus(department.getStatus());
    vo.setUserCount(department.getUserCount() != null ? department.getUserCount() : 0L);
    vo.setPath(department.getPath());

    // Set auditing fields
    vo.setTenantId(department.getTenantId());
    vo.setCreatedBy(department.getCreatedBy());
    vo.setCreator(department.getCreatedByName());
    vo.setCreatedDate(department.getCreatedDate());
    vo.setModifiedBy(department.getModifiedBy());
    vo.setModifier(department.getModifiedByName());
    vo.setModifiedDate(department.getModifiedDate());

    return vo;
  }

  /**
   * Convert Domain to ListVo
   */
  public static DepartmentListVo toListVo(Department department) {
    DepartmentListVo vo = new DepartmentListVo();
    vo.setId(department.getId());
    vo.setName(department.getName());
    vo.setCode(department.getCode());
    vo.setParentId(department.getParentId());
    vo.setParentName(department.getParentName());
    vo.setLevel(department.getLevel());
    vo.setSortOrder(department.getSortOrder());
    vo.setManagerId(department.getLeaderId()); // Map leaderId to managerId in VO
    vo.setManagerName(department.getManagerName());
    vo.setManagerAvatar(department.getManagerAvatar());
    vo.setDescription(department.getDescription());
    vo.setStatus(department.getStatus());
    vo.setUserCount(department.getUserCount() != null ? department.getUserCount() : 0L);
    vo.setPath(department.getPath());

    // Set auditing fields
    vo.setTenantId(department.getTenantId());
    vo.setCreatedBy(department.getCreatedBy());
    vo.setCreator(department.getCreatedByName());
    vo.setCreatedDate(department.getCreatedDate());
    vo.setModifiedBy(department.getModifiedBy());
    vo.setModifier(department.getModifiedByName());
    vo.setModifiedDate(department.getModifiedDate());

    return vo;
  }

  /**
   * Build query specification from FindDto
   */
  public static GenericSpecification<Department> getSpecification(DepartmentFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<DepartmentFindDto>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate", "name", "level")
        .matchSearchFields("name", "code")
        .build();
    return new GenericSpecification<>(filters);
  }

  /**
   * Convert User to DepartmentMemberVo
   */
  public static DepartmentMemberVo toMemberVo(User user, Long departmentId) {
    DepartmentMemberVo vo = new DepartmentMemberVo();
    vo.setId(user.getId());
    vo.setName(user.getName());
    vo.setEmail(user.getEmail());
    vo.setPhone(user.getPhone());
    vo.setAvatar(user.getAvatar());
    vo.setRole(user.getRole());
    vo.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
    
    // Check if user is manager of the department
    vo.setIsManager(user.getDepartmentId() != null && user.getDepartmentId().equals(departmentId));
    
    // Set join date (use createdDate as join date)
    if (user.getCreatedDate() != null) {
      vo.setJoinDate(user.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
    
    // Set auditing fields
    vo.setTenantId(user.getTenantId());
    vo.setCreatedBy(user.getCreatedBy());
    vo.setCreatedDate(user.getCreatedDate());
    vo.setModifiedBy(user.getModifiedBy());
    vo.setModifiedDate(user.getModifiedDate());
    
    return vo;
  }

  /**
   * Build query specification for department members
   */
  public static GenericSpecification<User> getMemberSpecification(Long departmentId, DepartmentMemberFindDto dto) {
    Set<SearchCriteria> filters = new java.util.HashSet<>();
    
    // Filter by departmentId
    filters.add(new SearchCriteria("departmentId", departmentId, SearchOperation.EQUAL));
    
    // Add keyword search if provided
    if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
      filters.add(new SearchCriteria("name", dto.getKeyword(), SearchOperation.MATCH));
    }
    
    return new GenericSpecification<>(filters);
  }
}
