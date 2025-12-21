package cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.authenticationorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authenticationorization.enums.SubjectType;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.dto.AuthorizationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.dto.AuthorizationFindDto;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.dto.AuthorizationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.vo.AuthorizationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.vo.AuthorizationListVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.beans.BeanUtils;

/**
 * Authorization Assembler
 * Converts between domain entities, DTOs, and VOs
 */
public class AuthorizationAssembler {

  /**
   * Convert CreateDto to domain entity
   */
  public static Authorization toDomain(AuthorizationCreateDto dto) {
    Authorization authorization = new Authorization();
    authorization.setSubjectType(dto.getSubjectType());
    authorization.setSubjectId(dto.getSubjectId());
    authorization.setPolicyId(dto.getPolicyId());
    authorization.setValidFrom(dto.getValidFrom());
    authorization.setValidTo(dto.getValidTo());
    authorization.setRemark(dto.getRemark());
    return authorization;
  }

  /**
   * Convert UpdateDto to domain entity with id
   */
  public static Authorization toUpdateDomain(Long id, AuthorizationUpdateDto dto) {
    Authorization authorization = new Authorization();
    authorization.setId(id);
    if (dto != null) {
      authorization.setSubjectType(dto.getSubjectType());
      authorization.setSubjectId(dto.getSubjectId());
      authorization.setPolicyId(dto.getPolicyId());
      authorization.setValidFrom(dto.getValidFrom());
      authorization.setValidTo(dto.getValidTo());
      authorization.setRemark(dto.getRemark());
    }
    return authorization;
  }

  /**
   * Convert UpdateDto to domain entity (legacy)
   */
  public static Authorization toDomain(AuthorizationUpdateDto dto) {
    Authorization authorization = new Authorization();
    authorization.setId(dto.getId());
    authorization.setSubjectType(dto.getSubjectType());
    authorization.setSubjectId(dto.getSubjectId());
    authorization.setPolicyId(dto.getPolicyId());
    authorization.setValidFrom(dto.getValidFrom());
    authorization.setValidTo(dto.getValidTo());
    authorization.setRemark(dto.getRemark());
    return authorization;
  }

  /**
   * Convert domain entity to DetailVo
   */
  public static AuthorizationDetailVo toDetailVo(Authorization authorization) {
    if (authorization == null) {
      return null;
    }
    AuthorizationDetailVo vo = new AuthorizationDetailVo();
    BeanUtils.copyProperties(authorization, vo);
    return vo;
  }

  /**
   * Convert domain entity to ListVo
   */
  public static AuthorizationListVo toListVo(Authorization authorization) {
    if (authorization == null) {
      return null;
    }
    AuthorizationListVo vo = new AuthorizationListVo();
    BeanUtils.copyProperties(authorization, vo);
    return vo;
  }

  /**
   * Build specification from find DTO
   */
  public static GenericSpecification<Authorization> getSpecification(AuthorizationFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate")
        .build();
    return new GenericSpecification<>(filters);
  }

  /**
   * Build specification with subject type filter
   */
  public static GenericSpecification<Authorization> getSpecificationByType(
      AuthorizationFindDto dto, SubjectType subjectType) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate")
        .build();
    // Add subject type filter
    filters.add(SearchCriteria.of("subjectType", subjectType.name()));
    return new GenericSpecification<>(filters);
  }
}
