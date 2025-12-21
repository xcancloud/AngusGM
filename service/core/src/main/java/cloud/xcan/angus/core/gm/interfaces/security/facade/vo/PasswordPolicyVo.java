package cloud.xcan.angus.core.gm.interfaces.security.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PasswordPolicyVo {
    private Long tenantId;
    private Long createdBy;
    private String creator;
    private LocalDateTime createdDate;
    private Long modifiedBy;
    private String modifier;
    private LocalDateTime modifiedDate;
    
    private Long id;
    private Integer minLength;
    private Integer maxLength;
    private Boolean requireUppercase;
    private Boolean requireLowercase;
    private Boolean requireNumbers;
    private Boolean requireSpecialChars;
    private Integer preventReuse;
    private Integer expirationDays;
    private Integer warningDays;
    private Integer maxLoginAttempts;
    private Integer lockoutDuration;
    private Boolean isEnabled;
}
