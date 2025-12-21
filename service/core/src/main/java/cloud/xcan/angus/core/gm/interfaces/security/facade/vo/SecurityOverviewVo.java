package cloud.xcan.angus.core.gm.interfaces.security.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SecurityOverviewVo {
    private Boolean passwordPolicyEnabled;
    private Boolean twoFactorAuthEnabled;
    private Boolean ipWhitelistEnabled;
    private Integer loginAttemptLimit;
    private Integer sessionTimeout;
    private LocalDateTime lastSecurityAudit;
    private String securityLevel;
}
