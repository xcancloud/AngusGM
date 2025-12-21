package cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.vo;

import cloud.xcan.angus.core.gm.domain.authenticationorization.enums.AuthorizationStatus;
import cloud.xcan.angus.core.gm.domain.authenticationorization.enums.SubjectType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Authorization List VO
 */
@Data
public class AuthorizationListVo {
    
    /**
     * Authorization ID
     */
    private Long id;
    
    /**
     * Subject type (USER, DEPARTMENT, GROUP)
     */
    private SubjectType subjectType;
    
    /**
     * Subject ID
     */
    private Long subjectId;
    
    /**
     * Policy ID
     */
    private Long policyId;
    
    /**
     * Authorization status
     */
    private AuthorizationStatus status;
    
    /**
     * Valid from date
     */
    private LocalDateTime validFrom;
    
    /**
     * Valid to date
     */
    private LocalDateTime validTo;
    
    /**
     * Create time
     */
    private LocalDateTime createTime;
}
