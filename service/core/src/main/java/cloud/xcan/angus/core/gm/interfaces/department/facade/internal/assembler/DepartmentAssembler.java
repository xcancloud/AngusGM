package cloud.xcan.angus.core.gm.interfaces.department.facade.internal.assembler;

import static cloud.xcan.angus.spec.BizConstant.nullSafe;

import cloud.xcan.angus.core.gm.domain.department.Department;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentCreateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentFindDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentListVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
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
    department.setLeaderId(dto.getLeaderId());
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
    department.setParentId(dto.getParentId());
    department.setLeaderId(dto.getLeaderId());
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
    vo.setLeaderId(department.getLeaderId());
    vo.setLeaderName(department.getLeaderName());
    vo.setDescription(department.getDescription());
    vo.setStatus(department.getStatus());
    vo.setMemberCount(nullSafe(department.getMemberCount(), 0L));

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
    vo.setLeaderId(department.getLeaderId());
    vo.setLeaderName(department.getLeaderName());
    vo.setDescription(department.getDescription());
    vo.setStatus(department.getStatus());
    vo.setMemberCount(nullSafe(department.getMemberCount(), 0L));

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
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate", "sortOrder")
        .orderByFields("id", "createdDate", "modifiedDate", "name", "sortOrder")
        .matchSearchFields("name", "code")
        .build();
    return new GenericSpecification<>(filters);
  }
}
