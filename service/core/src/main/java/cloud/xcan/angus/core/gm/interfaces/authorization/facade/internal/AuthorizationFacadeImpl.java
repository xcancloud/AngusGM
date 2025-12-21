package cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.internal;

import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;
import static cloud.xcan.angus.core.utils.CoreUtils.getMatchSearchFields;

import cloud.xcan.angus.core.gm.application.cmd.authenticationorization.AuthorizationCmd;
import cloud.xcan.angus.core.gm.application.query.authenticationorization.AuthorizationQuery;
import cloud.xcan.angus.core.gm.domain.authenticationorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authenticationorization.enums.SubjectType;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.AuthorizationFacade;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.dto.AuthorizationBatchCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.dto.AuthorizationBatchDeleteDto;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.dto.AuthorizationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.dto.AuthorizationFindDto;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.dto.AuthorizationRoleAddDto;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.dto.AuthorizationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.internal.assembler.AuthorizationAssembler;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.vo.AuthorizationBatchVo;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.vo.AuthorizationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.vo.AuthorizationListVo;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.vo.AuthorizationRoleAddVo;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.vo.AuthorizationTargetVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Authorization Facade Implementation
 */
@Component
public class AuthorizationFacadeImpl implements AuthorizationFacade {

  @Resource
  private AuthorizationCmd authorizationCmd;

  @Resource
  private AuthorizationQuery authorizationQuery;

  @Override
  public AuthorizationDetailVo create(AuthorizationCreateDto dto) {
    Authorization authorization = AuthorizationAssembler.toDomain(dto);
    authorization = authorizationCmd.create(authorization);
    return AuthorizationAssembler.toDetailVo(authorization);
  }

  @Override
  public AuthorizationDetailVo update(Long id, AuthorizationUpdateDto dto) {
    Authorization authorization = AuthorizationAssembler.toUpdateDomain(id, dto);
    authorization = authorizationCmd.update(authorization);
    return AuthorizationAssembler.toDetailVo(authorization);
  }

  @Override
  public void delete(Long id) {
    authorizationCmd.delete(id);
  }

  @Override
  public AuthorizationDetailVo getDetail(Long id) {
    Authorization authorization = authorizationQuery.findAndCheck(id);
    return AuthorizationAssembler.toDetailVo(authorization);
  }

  @Override
  public PageResult<AuthorizationListVo> list(AuthorizationFindDto dto) {
    GenericSpecification<Authorization> spec = AuthorizationAssembler.getSpecification(dto);
    Page<Authorization> page = authorizationQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, AuthorizationAssembler::toListVo);
  }

  @Override
  public PageResult<AuthorizationListVo> listUsers(AuthorizationFindDto dto) {
    GenericSpecification<Authorization> spec = AuthorizationAssembler.getSpecificationByType(dto, SubjectType.USER);
    Page<Authorization> page = authorizationQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, AuthorizationAssembler::toListVo);
  }

  @Override
  public PageResult<AuthorizationListVo> listDepartments(AuthorizationFindDto dto) {
    GenericSpecification<Authorization> spec = AuthorizationAssembler.getSpecificationByType(dto, SubjectType.DEPARTMENT);
    Page<Authorization> page = authorizationQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, AuthorizationAssembler::toListVo);
  }

  @Override
  public PageResult<AuthorizationListVo> listGroups(AuthorizationFindDto dto) {
    GenericSpecification<Authorization> spec = AuthorizationAssembler.getSpecificationByType(dto, SubjectType.GROUP);
    Page<Authorization> page = authorizationQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, AuthorizationAssembler::toListVo);
  }

  @Override
  public AuthorizationBatchVo batchCreate(AuthorizationBatchCreateDto dto) {
    // TODO: Implement batch create
    AuthorizationBatchVo result = new AuthorizationBatchVo();
    result.setSuccessCount(0);
    result.setFailedCount(0);
    result.setFailedItems(new ArrayList<>());
    return result;
  }

  @Override
  public void batchDelete(AuthorizationBatchDeleteDto dto) {
    if (dto.getIds() != null) {
      for (Long id : dto.getIds()) {
        authorizationCmd.delete(id);
      }
    }
  }

  @Override
  public AuthorizationTargetVo getTargetAuthorization(String targetType, String targetId) {
    // TODO: Implement target authorization query
    return new AuthorizationTargetVo();
  }

  @Override
  public AuthorizationRoleAddVo addRoles(Long id, AuthorizationRoleAddDto dto) {
    // TODO: Implement add roles
    AuthorizationRoleAddVo result = new AuthorizationRoleAddVo();
    result.setId(id);
    result.setAddedRoles(dto.getRoleIds());
    return result;
  }

  @Override
  public void removeRole(Long id, Long roleId) {
    // TODO: Implement remove role
  }
}
