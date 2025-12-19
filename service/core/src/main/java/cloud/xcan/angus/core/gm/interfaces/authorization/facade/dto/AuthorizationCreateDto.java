package cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto;

import cloud.xcan.angus.core.gm.domain.authorization.enums.SubjectType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Authorization Create DTO
 */
@Data
public class AuthorizationCreateDto {
    
    /**
     * Subject type (USER, DEPARTMENT, GROUP)
     */
    @NotNull
    private SubjectType subjectType;
    
    /**
     * Subject ID
     */
    @NotNull
    private Long subjectId;
    
    /**
     * Policy ID
     */
    @NotNull
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
