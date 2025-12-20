package cloud.xcan.angus.core.gm.application.cmd.authorization;

import cloud.xcan.angus.core.gm.domain.authorization.Authorization;

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
     * Enable an authorization
     */
    void enable(Long id);
    
    /**
     * Disable an authorization
     */
    void disable(Long id);
    
    /**
     * Delete an authorization
     */
    void delete(Long id);
}
