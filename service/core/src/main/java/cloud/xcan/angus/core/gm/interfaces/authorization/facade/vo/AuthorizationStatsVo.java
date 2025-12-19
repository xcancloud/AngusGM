package cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo;

import lombok.Data;

/**
 * Authorization Statistics VO
 */
@Data
public class AuthorizationStatsVo {
    
    /**
     * Total authorization count
     */
    private Long total;
    
    /**
     * Enabled authorization count
     */
    private Long enabled;
    
    /**
     * Disabled authorization count
     */
    private Long disabled;
    
    /**
     * User authorizations count
     */
    private Long userCount;
    
    /**
     * Department authorizations count
     */
    private Long departmentCount;
    
    /**
     * Group authorizations count
     */
    private Long groupCount;
}
