package cloud.xcan.angus.core.gm.interfaces.authorization.facade;

import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationFindDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationListVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationStatsVo;
import org.springframework.data.domain.Page;

/**
 * Authorization Facade
 */
public interface AuthorizationFacade {
    
    /**
     * Create authorization
     */
    AuthorizationDetailVo create(AuthorizationCreateDto dto);
    
    /**
     * Update authorization
     */
    AuthorizationDetailVo update(AuthorizationUpdateDto dto);
    
    /**
     * Enable authorization
     */
    void enable(Long id);
    
    /**
     * Disable authorization
     */
    void disable(Long id);
    
    /**
     * Delete authorization
     */
    void delete(Long id);
    
    /**
     * Get authorization details
     */
    AuthorizationDetailVo getById(Long id);
    
    /**
     * Find authorizations
     */
    Page<AuthorizationListVo> find(AuthorizationFindDto dto);
    
    /**
     * Get authorization statistics
     */
    AuthorizationStatsVo getStats();
}
