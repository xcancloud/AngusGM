package cloud.xcan.angus.core.gm.application.cmd.authorization;

import cloud.xcan.angus.core.gm.domain.authenticationorization.Authorization;

import java.util.List;

/**
 * Authorization Command Service
 * Handles write operations for authorization management
 */
public interface AuthorizationCmd {
    
    /**
     * Create a new authorization
     */
    Authorization create(Authorization authorization);
    
    /**
     * Update an existing authorization
     */
    Authorization update(Authorization authorization);
    
    /**
     * Delete an authorization
     */
    void delete(Long id);
    
    /**
     * Add roles to authorization
     */
    void addRoles(Long authorizationId, List<Long> roleIds);
    
    /**
     * Remove role from authorization
     */
    void removeRole(Long authorizationId, Long roleId);
    
    /**
     * Batch create authorizations
     */
    int batchCreate(String targetType, List<Long> targetIds, List<Long> roleIds);
    
    /**
     * Batch delete authorizations
     */
    void batchDelete(List<Long> authorizationIds);
}
