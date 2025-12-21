package cloud.xcan.angus.core.gm.interfaces.security.facade;

import cloud.xcan.angus.common.result.PageResult;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.*;

public interface SecurityFacade {
    
    // ==================== 概览 ====================
    
    SecurityOverviewVo getOverview();
    
    // ==================== 密码策略 ====================
    
    PasswordPolicyVo getPasswordPolicy();
    
    PasswordPolicyVo updatePasswordPolicy(PasswordPolicyUpdateDto dto);
    
    // ==================== 双因素认证 ====================
    
    TwoFactorConfigVo getTwoFactorConfig();
    
    TwoFactorConfigVo updateTwoFactorConfig(TwoFactorConfigUpdateDto dto);
    
    // ==================== IP白名单管理 ====================
    
    PageResult<IpWhitelistVo> listIpWhitelist(IpWhitelistFindDto dto);
    
    IpWhitelistVo addIpWhitelist(IpWhitelistCreateDto dto);
    
    IpWhitelistVo updateIpWhitelist(Long id, IpWhitelistUpdateDto dto);
    
    void deleteIpWhitelist(Long id);
    
    // ==================== 会话管理 ====================
    
    SessionConfigVo getSessionConfig();
    
    SessionConfigVo updateSessionConfig(SessionConfigUpdateDto dto);
    
    PageResult<ActiveSessionVo> listActiveSessions(ActiveSessionFindDto dto);
    
    void terminateSession(String sessionId);
    
    // ==================== 安全审计 ====================
    
    PageResult<SecurityEventVo> listSecurityEvents(SecurityEventFindDto dto);
    
    SecurityEventHandleVo handleSecurityEvent(Long id, SecurityEventHandleDto dto);
    
    SecurityAuditStatsVo getAuditStats(SecurityAuditStatsFindDto dto);
}
