package cloud.xcan.angus.core.gm.application.query.security;

import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.*;

/**
 * <p>Security query service interface</p>
 */
public interface SecurityQuery {
    
    /**
     * <p>Get password policy</p>
     */
    PasswordPolicyVo getPasswordPolicy();
    
    /**
     * <p>Get two factor config</p>
     */
    TwoFactorConfigVo getTwoFactorConfig();
    
    /**
     * <p>List IP whitelist</p>
     */
    PageResult<IpWhitelistVo> listIpWhitelist(IpWhitelistFindDto dto);
    
    /**
     * <p>Get session config</p>
     */
    SessionConfigVo getSessionConfig();
    
    /**
     * <p>List active sessions</p>
     */
    PageResult<ActiveSessionVo> listActiveSessions(ActiveSessionFindDto dto);
    
    /**
     * <p>List security events</p>
     */
    PageResult<SecurityEventVo> listSecurityEvents(SecurityEventFindDto dto);
    
    /**
     * <p>Get audit stats</p>
     */
    SecurityAuditStatsVo getAuditStats(SecurityAuditStatsFindDto dto);
}
