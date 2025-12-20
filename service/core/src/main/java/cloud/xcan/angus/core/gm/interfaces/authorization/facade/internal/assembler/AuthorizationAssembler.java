package cloud.xcan.angus.core.gm.interfaces.authorization.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.authorization.Authorization;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationListVo;
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
     * Convert UpdateDto to domain entity
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
}
