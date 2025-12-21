package cloud.xcan.angus.core.gm.interfaces.authorization.facade.internal;

import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;
import static cloud.xcan.angus.core.utils.CoreUtils.getMatchSearchFields;

import cloud.xcan.angus.core.gm.application.cmd.authorization.AuthorizationCmd;
import cloud.xcan.angus.core.gm.application.query.authorization.AuthorizationQuery;
import cloud.xcan.angus.core.gm.domain.authorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRepo;
import cloud.xcan.angus.core.gm.domain.authorization.enums.SubjectType;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.AuthorizationFacade;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationBatchCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationBatchDeleteDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationFindDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationRoleAddDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.internal.assembler.AuthorizationAssembler;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationBatchVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationListVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationRoleAddVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationTargetVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.remote.search.SearchOperation;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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

  @Resource
  private AuthorizationRepo authorizationRepo;

  @Override
  public AuthorizationDetailVo create(AuthorizationCreateDto dto) {
    Authorization authorization = AuthorizationAssembler.toDomain(dto);
    authorization = authorizationCmd.create(authorization);
    return AuthorizationAssembler.toDetailVo(authorization);
  }

  @Override
  public AuthorizationBatchVo batchCreate(AuthorizationBatchCreateDto dto) {
    AuthorizationBatchVo result = new AuthorizationBatchVo();
    result.setTargetType(dto.getTargetType());
    result.setTargetCount(dto.getTargetIds() != null ? dto.getTargetIds().size() : 0);
    result.setRoleCount(dto.getRoleIds() != null ? dto.getRoleIds().size() : 0);
    
    List<AuthorizationBatchVo.BatchResultItem> results = new ArrayList<>();
    int successCount = 0;
    int failedCount = 0;
    
    if (dto.getTargetIds() != null && dto.getRoleIds() != null) {
      // Call batch create command
      try {
        int createdCount = authorizationCmd.batchCreate(
            dto.getTargetType(),
            dto.getTargetIds(),
            dto.getRoleIds()
        );
        successCount = createdCount;
        
        // Create success results for all targets
        for (Long targetId : dto.getTargetIds()) {
          AuthorizationBatchVo.BatchResultItem item = new AuthorizationBatchVo.BatchResultItem();
          item.setTargetId(targetId);
          item.setSuccess(true);
          results.add(item);
        }
      } catch (Exception e) {
        // If batch create fails, mark all as failed
        failedCount = dto.getTargetIds().size();
        for (Long targetId : dto.getTargetIds()) {
          AuthorizationBatchVo.BatchResultItem item = new AuthorizationBatchVo.BatchResultItem();
          item.setTargetId(targetId);
          item.setSuccess(false);
          item.setErrorMessage(e.getMessage());
          results.add(item);
        }
      }
    }
    
    result.setSuccessCount(successCount);
    result.setFailedCount(failedCount);
    result.setResults(results);
    return result;
  }

  @Override
  public AuthorizationDetailVo update(Long id, AuthorizationUpdateDto dto) {
    Authorization authorization = AuthorizationAssembler.toUpdateDomain(id, dto);
    authorization = authorizationCmd.update(authorization);
    return AuthorizationAssembler.toDetailVo(authorization);
  }

  @Override
  public AuthorizationRoleAddVo addRoles(Long id, AuthorizationRoleAddDto dto) {
    // Verify authorization exists
    Authorization authorization = authorizationQuery.findAndCheck(id);
    
    // Add roles to authorization
    if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
      authorizationCmd.addRoles(id, dto.getRoleIds());
    }
    
    // Reload authorization to get updated data
    authorization = authorizationQuery.findAndCheck(id);
    
    // Build result
    AuthorizationRoleAddVo result = new AuthorizationRoleAddVo();
    result.setAuthorizationId(id);
    result.setModifiedDate(authorization.getModifiedDate());
    
    // Build added roles list
    List<AuthorizationRoleAddVo.AddedRole> addedRoles = new ArrayList<>();
    if (dto.getRoleIds() != null) {
      for (Long roleId : dto.getRoleIds()) {
        AuthorizationRoleAddVo.AddedRole role = new AuthorizationRoleAddVo.AddedRole();
        role.setId(roleId);
        // TODO: Fetch role name from role service if needed
        addedRoles.add(role);
      }
    }
    result.setAddedRoles(addedRoles);
    
    return result;
  }

  @Override
  public void removeRole(Long id, Long roleId) {
    // Verify authorization exists
    authorizationQuery.findAndCheck(id);
    
    // Remove role from authorization
    authorizationCmd.removeRole(id, roleId);
  }

  @Override
  public void delete(Long id) {
    authorizationCmd.delete(id);
  }

  @Override
  public void batchDelete(AuthorizationBatchDeleteDto dto) {
    if (dto.getAuthorizationIds() != null && !dto.getAuthorizationIds().isEmpty()) {
      authorizationCmd.batchDelete(dto.getAuthorizationIds());
    }
  }

  @Override
  public AuthorizationDetailVo getDetail(Long id) {
    Authorization authorization = authorizationQuery.findAndCheck(id);
    return AuthorizationAssembler.toDetailVo(authorization);
  }

  @Override
  public AuthorizationTargetVo getTargetAuthorization(String targetType, String targetId) {
    // Convert targetType string to SubjectType enum
    SubjectType subjectType;
    try {
      subjectType = SubjectType.valueOf(targetType.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid target type: " + targetType);
    }
    
    // Convert targetId string to Long
    Long subjectId;
    try {
      subjectId = Long.parseLong(targetId);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid target id: " + targetId);
    }
    
    // Build specification to query authorizations by subject type and subject id
    Set<SearchCriteria> filters = new HashSet<>();
    filters.add(new SearchCriteria("subjectType", subjectType.name(), SearchOperation.EQUAL));
    filters.add(new SearchCriteria("subjectId", subjectId, SearchOperation.EQUAL));
    GenericSpecification<Authorization> spec = new GenericSpecification<>(filters);
    
    // Query all authorizations for this target directly without pagination
    List<Authorization> authorizations = authorizationRepo.findAll(spec);
    
    // Build result
    AuthorizationTargetVo result = new AuthorizationTargetVo();
    result.setTargetType(targetType);
    result.setTargetId(subjectId);
    // TODO: Fetch target name from user/department/group service if needed
    
    // Extract unique role IDs from authorizations
    List<Long> roleIds = authorizations.stream()
        .map(Authorization::getPolicyId)
        .distinct()
        .collect(Collectors.toList());
    
    // Build role info list
    List<AuthorizationTargetVo.RoleInfo> roles = new ArrayList<>();
    for (Long roleId : roleIds) {
      AuthorizationTargetVo.RoleInfo roleInfo = new AuthorizationTargetVo.RoleInfo();
      roleInfo.setId(roleId);
      // TODO: Fetch role details (name, code, appId, appName) from role service if needed
      roles.add(roleInfo);
    }
    result.setRoles(roles);
    
    // TODO: Build permissions list from roles if needed
    result.setPermissions(new ArrayList<>());
    
    return result;
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

  // TODO: 添加统计相关接口
}
