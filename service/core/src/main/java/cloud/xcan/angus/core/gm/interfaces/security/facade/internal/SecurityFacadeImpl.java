package cloud.xcan.angus.core.gm.interfaces.security.facade.internal;

import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.core.gm.application.cmd.security.SecurityCmd;
import cloud.xcan.angus.core.gm.application.query.security.SecurityQuery;
import cloud.xcan.angus.core.gm.interfaces.security.facade.SecurityFacade;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Implementation of security facade</p>
 */
@Service
public class SecurityFacadeImpl implements SecurityFacade {
    
    @Resource
    private SecurityCmd securityCmd;
    
    @Resource
    private SecurityQuery securityQuery;
    
    @Override
    public SecurityOverviewVo getOverview() {
        // TODO: Implement getOverview logic
        SecurityOverviewVo vo = new SecurityOverviewVo();
        vo.setPasswordPolicyEnabled(true);
        vo.setTwoFactorAuthEnabled(true);
        vo.setIpWhitelistEnabled(false);
        vo.setLoginAttemptLimit(5);
        vo.setSessionTimeout(7200);
        vo.setSecurityLevel("高");
        return vo;
    }
    
    @Override
    public PasswordPolicyVo getPasswordPolicy() {
        return securityQuery.getPasswordPolicy();
    }
    
    @Override
    public PasswordPolicyVo updatePasswordPolicy(PasswordPolicyUpdateDto dto) {
        return securityCmd.updatePasswordPolicy(dto);
    }
    
    @Override
    public TwoFactorConfigVo getTwoFactorConfig() {
        return securityQuery.getTwoFactorConfig();
    }
    
    @Override
    public TwoFactorConfigVo updateTwoFactorConfig(TwoFactorConfigUpdateDto dto) {
        return securityCmd.updateTwoFactorConfig(dto);
    }
    
    @Override
    public PageResult<IpWhitelistVo> listIpWhitelist(IpWhitelistFindDto dto) {
        return securityQuery.listIpWhitelist(dto);
    }
    
    @Override
    public IpWhitelistVo addIpWhitelist(IpWhitelistCreateDto dto) {
        return securityCmd.addIpWhitelist(dto);
    }
    
    @Override
    public IpWhitelistVo updateIpWhitelist(Long id, IpWhitelistUpdateDto dto) {
        return securityCmd.updateIpWhitelist(id, dto);
    }
    
    @Override
    public void deleteIpWhitelist(Long id) {
        securityCmd.deleteIpWhitelist(id);
    }
    
    @Override
    public SessionConfigVo getSessionConfig() {
        return securityQuery.getSessionConfig();
    }
    
    @Override
    public SessionConfigVo updateSessionConfig(SessionConfigUpdateDto dto) {
        return securityCmd.updateSessionConfig(dto);
    }
    
    @Override
    public PageResult<ActiveSessionVo> listActiveSessions(ActiveSessionFindDto dto) {
        return securityQuery.listActiveSessions(dto);
    }
    
    @Override
    public void terminateSession(String sessionId) {
        securityCmd.terminateSession(sessionId);
    }
    
    @Override
    public PageResult<SecurityEventVo> listSecurityEvents(SecurityEventFindDto dto) {
        return securityQuery.listSecurityEvents(dto);
    }
    
    @Override
    public SecurityEventHandleVo handleSecurityEvent(Long id, SecurityEventHandleDto dto) {
        return securityCmd.handleSecurityEvent(id, dto);
    }
    
    @Override
    public SecurityAuditStatsVo getAuditStats(SecurityAuditStatsFindDto dto) {
        return securityQuery.getAuditStats(dto);
    }
}
