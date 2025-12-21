package cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.dto;

import cloud.xcan.angus.core.gm.domain.authenticationorization.enums.AuthorizationStatus;
import cloud.xcan.angus.core.gm.domain.authenticationorization.enums.SubjectType;
import lombok.Data;

/**
 * Authorization Find DTO
 */
@Data
public class AuthorizationFindDto {
    
    /**
     * Authorization status
     */
    private AuthorizationStatus status;
    
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
     * Page number
     */
    private Integer page = 0;
    
    /**
     * Page size
     */
    private Integer size = 20;
}
