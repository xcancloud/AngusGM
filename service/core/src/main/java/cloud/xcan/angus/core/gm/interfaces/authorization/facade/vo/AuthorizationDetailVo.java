package cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo;

import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationStatus;
import cloud.xcan.angus.core.gm.domain.authorization.enums.SubjectType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Authorization Detail VO
 */
@Data
public class AuthorizationDetailVo {
    
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
     * Remark
     */
    private String remark;
    
    /**
     * Create time
     */
    private LocalDateTime createTime;
    
    /**
     * Update time
     */
    private LocalDateTime updateTime;
}
