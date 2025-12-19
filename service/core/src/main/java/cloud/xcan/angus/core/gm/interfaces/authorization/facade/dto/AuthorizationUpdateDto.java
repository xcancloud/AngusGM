package cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto;

import cloud.xcan.angus.core.gm.domain.authorization.enums.SubjectType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Authorization Update DTO
 */
@Data
public class AuthorizationUpdateDto {
    
    /**
     * Authorization ID
     */
    @NotNull
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
}
