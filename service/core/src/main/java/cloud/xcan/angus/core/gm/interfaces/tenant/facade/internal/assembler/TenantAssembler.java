package cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler;

import static cloud.xcan.angus.spec.BizConstant.nullSafe;

import cloud.xcan.angus.core.gm.domain.tenant.Tenant;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantCreateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantFindDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantDetailVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantListVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Tenant assembler for DTO/Domain/VO conversion
 */
public class TenantAssembler {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Convert CreateDto to Domain
   */
  public static Tenant toCreateDomain(TenantCreateDto dto) {
    Tenant tenant = new Tenant();
    tenant.setName(dto.getName());
    tenant.setCode(dto.getCode());
    tenant.setType(dto.getType());
    tenant.setAccountType(dto.getAccountType());
    tenant.setAdminName(dto.getAdminName());
    tenant.setAdminEmail(dto.getAdminEmail());
    tenant.setAdminPhone(dto.getAdminPhone());
    tenant.setAddress(dto.getAddress());
    tenant.setStatus(dto.getStatus());
    tenant.setLogo(dto.getLogo());
    if (dto.getExpireDate() != null) {
      tenant.setExpireDate(LocalDate.parse(dto.getExpireDate(), DATE_FORMATTER));
    }
    return tenant;
  }

  /**
   * Convert UpdateDto to Domain
   */
  public static Tenant toUpdateDomain(Long id, TenantUpdateDto dto) {
    Tenant tenant = new Tenant();
    tenant.setId(id);
    tenant.setName(dto.getName());
    tenant.setType(dto.getType());
    tenant.setAdminName(dto.getAdminName());
    tenant.setAdminEmail(dto.getAdminEmail());
    tenant.setAdminPhone(dto.getAdminPhone());
    tenant.setAddress(dto.getAddress());
    tenant.setStatus(dto.getStatus());
    tenant.setLogo(dto.getLogo());
    if (dto.getExpireDate() != null) {
      tenant.setExpireDate(LocalDate.parse(dto.getExpireDate(), DATE_FORMATTER));
    }
    return tenant;
  }

  /**
   * Convert Domain to DetailVo
   */
  public static TenantDetailVo toDetailVo(Tenant tenant) {
    TenantDetailVo vo = new TenantDetailVo();
    vo.setId(tenant.getId());
    vo.setName(tenant.getName());
    vo.setCode(tenant.getCode());
    vo.setType(tenant.getType());
    vo.setAccountType(tenant.getAccountType());
    vo.setAdminName(tenant.getAdminName());
    vo.setAdminEmail(tenant.getAdminEmail());
    vo.setAdminPhone(tenant.getAdminPhone());
    vo.setUserCount(nullSafe(tenant.getUserCount(), 0L));
    vo.setDepartmentCount(nullSafe(tenant.getDepartmentCount(), 0L));
    vo.setStatus(tenant.getStatus());
    vo.setAddress(tenant.getAddress());
    vo.setExpireDate(tenant.getExpireDate() != null 
        ? tenant.getExpireDate().format(DATE_FORMATTER) : null);
    vo.setLogo(tenant.getLogo());

    // Set auditing fields
    vo.setTenantId(tenant.getTenantId());
    vo.setCreatedBy(tenant.getCreatedBy());
    vo.setCreator(tenant.getCreatedByName());
    vo.setCreatedDate(tenant.getCreatedDate());
    vo.setModifiedBy(tenant.getModifiedBy());
    vo.setModifier(tenant.getModifiedByName());
    vo.setModifiedDate(tenant.getModifiedDate());

    return vo;
  }

  /**
   * Convert Domain to ListVo
   */
  public static TenantListVo toListVo(Tenant tenant) {
    TenantListVo vo = new TenantListVo();
    vo.setId(tenant.getId());
    vo.setName(tenant.getName());
    vo.setCode(tenant.getCode());
    vo.setType(tenant.getType());
    vo.setAccountType(tenant.getAccountType());
    vo.setAdminName(tenant.getAdminName());
    vo.setAdminEmail(tenant.getAdminEmail());
    vo.setAdminPhone(tenant.getAdminPhone());
    vo.setUserCount(nullSafe(tenant.getUserCount(), 0L));
    vo.setDepartmentCount(nullSafe(tenant.getDepartmentCount(), 0L));
    vo.setStatus(tenant.getStatus());
    vo.setAddress(tenant.getAddress());
    vo.setExpireDate(tenant.getExpireDate() != null 
        ? tenant.getExpireDate().format(DATE_FORMATTER) : null);
    vo.setLogo(tenant.getLogo());

    // Set auditing fields
    vo.setTenantId(tenant.getTenantId());
    vo.setCreatedBy(tenant.getCreatedBy());
    vo.setCreator(tenant.getCreatedByName());
    vo.setCreatedDate(tenant.getCreatedDate());
    vo.setModifiedBy(tenant.getModifiedBy());
    vo.setModifier(tenant.getModifiedByName());
    vo.setModifiedDate(tenant.getModifiedDate());

    return vo;
  }

  /**
   * Build query specification from FindDto
   */
  public static GenericSpecification<Tenant> getSpecification(TenantFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate", "name")
        .matchSearchFields("name", "code")
        .build();
    return new GenericSpecification<>(filters);
  }
}
