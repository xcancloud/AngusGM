package cloud.xcan.angus.core.gm.application.cmd.security;

import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.*;

/**
 * <p>Security command service interface</p>
 */
public interface SecurityCmd {
    
    /**
     * <p>Update password policy</p>
     */
    cloud.xcan.angus.core.gm.interfaces.security.facade.vo.PasswordPolicyVo updatePasswordPolicy(PasswordPolicyUpdateDto dto);
    
    /**
     * <p>Update two factor config</p>
     */
    cloud.xcan.angus.core.gm.interfaces.security.facade.vo.TwoFactorConfigVo updateTwoFactorConfig(TwoFactorConfigUpdateDto dto);
    
    /**
     * <p>Add IP whitelist</p>
     */
    cloud.xcan.angus.core.gm.interfaces.security.facade.vo.IpWhitelistVo addIpWhitelist(IpWhitelistCreateDto dto);
    
    /**
     * <p>Update IP whitelist</p>
     */
    cloud.xcan.angus.core.gm.interfaces.security.facade.vo.IpWhitelistVo updateIpWhitelist(Long id, IpWhitelistUpdateDto dto);
    
    /**
     * <p>Delete IP whitelist</p>
     */
    void deleteIpWhitelist(Long id);
    
    /**
     * <p>Update session config</p>
     */
    cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SessionConfigVo updateSessionConfig(SessionConfigUpdateDto dto);
    
    /**
     * <p>Terminate session</p>
     */
    void terminateSession(String sessionId);
    
    /**
     * <p>Handle security event</p>
     */
    cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityEventHandleVo handleSecurityEvent(Long id, SecurityEventHandleDto dto);
}
