package cloud.xcan.angus.core.gm.interfaces.authorization.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.authorization.AuthorizationCmd;
import cloud.xcan.angus.core.gm.application.query.authorization.AuthorizationQuery;
import cloud.xcan.angus.core.gm.domain.authorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationStatus;
import cloud.xcan.angus.core.gm.domain.authorization.enums.SubjectType;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.AuthorizationFacade;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationFindDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.internal.assembler.AuthorizationAssembler;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationListVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationStatsVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Authorization Facade Implementation
 */
@Service
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
    public AuthorizationDetailVo update(AuthorizationUpdateDto dto) {
        Authorization authorization = AuthorizationAssembler.toDomain(dto);
        authorization = authorizationCmd.update(authorization);
        return AuthorizationAssembler.toDetailVo(authorization);
    }
    
    @Override
    public void enable(Long id) {
        authorizationCmd.enable(id);
    }
    
    @Override
    public void disable(Long id) {
        authorizationCmd.disable(id);
    }
    
    @Override
    public void delete(Long id) {
        authorizationCmd.delete(id);
    }
    
    @Override
    public AuthorizationDetailVo getById(Long id) {
        Authorization authorization = authorizationQuery.findById(id);
        return AuthorizationAssembler.toDetailVo(authorization);
    }
    
    @Override
    public Page<AuthorizationListVo> find(AuthorizationFindDto dto) {
        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize());
        
        Page<Authorization> page;
        if (dto.getStatus() != null) {
            page = authorizationQuery.findByStatus(dto.getStatus(), pageRequest);
        } else if (dto.getSubjectType() != null) {
            page = authorizationQuery.findBySubjectType(dto.getSubjectType(), pageRequest);
        } else if (dto.getSubjectId() != null) {
            page = authorizationQuery.findBySubjectId(dto.getSubjectId(), pageRequest);
        } else if (dto.getPolicyId() != null) {
            page = authorizationQuery.findByPolicyId(dto.getPolicyId(), pageRequest);
        } else {
            page = authorizationQuery.findAll(pageRequest);
        }
        
        return page.map(AuthorizationAssembler::toListVo);
    }
    
    @Override
    public AuthorizationStatsVo getStats() {
        AuthorizationStatsVo stats = new AuthorizationStatsVo();
        stats.setTotal(authorizationQuery.count());
        stats.setEnabled(authorizationQuery.countByStatus(AuthorizationStatus.ENABLED));
        stats.setDisabled(authorizationQuery.countByStatus(AuthorizationStatus.DISABLED));
        stats.setUserCount(authorizationQuery.countBySubjectType(SubjectType.USER));
        stats.setDepartmentCount(authorizationQuery.countBySubjectType(SubjectType.DEPARTMENT));
        stats.setGroupCount(authorizationQuery.countBySubjectType(SubjectType.GROUP));
        return stats;
    }
}
