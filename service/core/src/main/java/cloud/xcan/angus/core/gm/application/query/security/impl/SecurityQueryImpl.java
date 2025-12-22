package cloud.xcan.angus.core.gm.application.query.security.impl;

import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.security.SecurityQuery;
import cloud.xcan.angus.core.gm.domain.security.Security;
import cloud.xcan.angus.core.gm.domain.security.SecurityRepo;
import cloud.xcan.angus.core.gm.domain.security.SecurityType;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Implementation of security query service</p>
 */
@Biz
@Service
@Transactional(readOnly = true)
public class SecurityQueryImpl implements SecurityQuery {
    
    @Resource
    private SecurityRepo securityRepo;
    
    @Resource
    private ObjectMapper objectMapper;
    
    @Override
    public PasswordPolicyVo getPasswordPolicy() {
        return new BizTemplate<PasswordPolicyVo>() {
            @Override
            protected PasswordPolicyVo process() {
                Security security = securityRepo.findByTypeAndScope(SecurityType.PASSWORD_POLICY, "DEFAULT")
                    .orElse(null);
                
                if (security == null) {
                    PasswordPolicyVo vo = new PasswordPolicyVo();
                    vo.setMinLength(8);
                    vo.setMaxLength(32);
                    vo.setRequireUppercase(true);
                    vo.setRequireLowercase(true);
                    vo.setRequireNumbers(true);
                    vo.setRequireSpecialChars(true);
                    vo.setPreventReuse(5);
                    vo.setExpirationDays(90);
                    vo.setWarningDays(7);
                    vo.setMaxLoginAttempts(5);
                    vo.setLockoutDuration(30);
                    vo.setIsEnabled(true);
                    return vo;
                }
                
                PasswordPolicyVo vo = new PasswordPolicyVo();
                vo.setId(security.getId());
                vo.setIsEnabled(security.getEnabled());
                
                try {
                    if (security.getConfig() != null) {
                        Map<String, Object> config = objectMapper.readValue(
                            security.getConfig().toString(), 
                            new TypeReference<Map<String, Object>>() {}
                        );
                        vo.setMinLength((Integer) config.get("minLength"));
                        vo.setMaxLength((Integer) config.get("maxLength"));
                        vo.setRequireUppercase((Boolean) config.get("requireUppercase"));
                        vo.setRequireLowercase((Boolean) config.get("requireLowercase"));
                        vo.setRequireNumbers((Boolean) config.get("requireNumbers"));
                        vo.setRequireSpecialChars((Boolean) config.get("requireSpecialChars"));
                        vo.setPreventReuse((Integer) config.get("preventReuse"));
                        vo.setExpirationDays((Integer) config.get("expirationDays"));
                        vo.setWarningDays((Integer) config.get("warningDays"));
                        vo.setMaxLoginAttempts((Integer) config.get("maxLoginAttempts"));
                        vo.setLockoutDuration((Integer) config.get("lockoutDuration"));
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to deserialize config", e);
                }
                
                return vo;
            }
        }.execute();
    }
    
    @Override
    public TwoFactorConfigVo getTwoFactorConfig() {
        return new BizTemplate<TwoFactorConfigVo>() {
            @Override
            protected TwoFactorConfigVo process() {
                Security security = securityRepo.findByTypeAndScope(SecurityType.TWO_FACTOR, "DEFAULT")
                    .orElse(null);
                
                if (security == null) {
                    TwoFactorConfigVo vo = new TwoFactorConfigVo();
                    vo.setIsEnabled(true);
                    vo.setMethods(List.of("sms", "email", "totp"));
                    vo.setDefaultMethod("sms");
                    vo.setCodeExpiration(300);
                    vo.setCodeLength(6);
                    vo.setTrustDeviceDays(30);
                    vo.setEnforceForAdmins(true);
                    vo.setEnforceForAllUsers(false);
                    return vo;
                }
                
                TwoFactorConfigVo vo = new TwoFactorConfigVo();
                vo.setId(security.getId());
                vo.setIsEnabled(security.getEnabled());
                
                try {
                    if (security.getConfig() != null) {
                        Map<String, Object> config = objectMapper.readValue(
                            security.getConfig().toString(), 
                            new TypeReference<Map<String, Object>>() {}
                        );
                        vo.setIsEnabled((Boolean) config.get("isEnabled"));
                        vo.setMethods((List<String>) config.get("methods"));
                        vo.setDefaultMethod((String) config.get("defaultMethod"));
                        vo.setCodeExpiration((Integer) config.get("codeExpiration"));
                        vo.setCodeLength(6);
                        vo.setTrustDeviceDays((Integer) config.get("trustDeviceDays"));
                        vo.setEnforceForAdmins((Boolean) config.get("enforceForAdmins"));
                        vo.setEnforceForAllUsers((Boolean) config.get("enforceForAllUsers"));
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to deserialize config", e);
                }
                
                return vo;
            }
        }.execute();
    }
    
    @Override
    public PageResult<IpWhitelistVo> listIpWhitelist(IpWhitelistFindDto dto) {
        return new BizTemplate<PageResult<IpWhitelistVo>>() {
            @Override
            protected PageResult<IpWhitelistVo> process() {
                Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getSize());
                Page<Security> page = securityRepo.findByType(SecurityType.IP_WHITELIST, pageable);
                
                List<IpWhitelistVo> vos = page.getContent().stream()
                    .map(security -> {
                        IpWhitelistVo vo = new IpWhitelistVo();
                        vo.setId(security.getId());
                        
                        try {
                            if (security.getConfig() != null) {
                                Map<String, Object> config = objectMapper.readValue(
                                    security.getConfig().toString(), 
                                    new TypeReference<Map<String, Object>>() {}
                                );
                                vo.setIpAddress((String) config.get("ipAddress"));
                                vo.setIpRange((String) config.get("ipRange"));
                                vo.setDescription((String) config.get("description"));
                                vo.setStatus((String) config.get("status"));
                                vo.setUsageCount(((Number) config.get("usageCount")).longValue());
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to deserialize config", e);
                        }
                        
                        return vo;
                    })
                    .collect(Collectors.toList());
                
                return PageResult.of(page.getTotalElements(), vos);
            }
        }.execute();
    }
    
    @Override
    public SessionConfigVo getSessionConfig() {
        return new BizTemplate<SessionConfigVo>() {
            @Override
            protected SessionConfigVo process() {
                Security security = securityRepo.findByTypeAndScope(SecurityType.SESSION_CONFIG, "DEFAULT")
                    .orElse(null);
                
                if (security == null) {
                    SessionConfigVo vo = new SessionConfigVo();
                    vo.setTimeout(7200);
                    vo.setMaxConcurrent(3);
                    vo.setAllowMultipleDevices(true);
                    vo.setAutoLogoutEnabled(true);
                    vo.setRememberMeDays(30);
                    return vo;
                }
                
                SessionConfigVo vo = new SessionConfigVo();
                vo.setId(security.getId());
                
                try {
                    if (security.getConfig() != null) {
                        Map<String, Object> config = objectMapper.readValue(
                            security.getConfig().toString(), 
                            new TypeReference<Map<String, Object>>() {}
                        );
                        vo.setTimeout((Integer) config.get("timeout"));
                        vo.setMaxConcurrent((Integer) config.get("maxConcurrent"));
                        vo.setAllowMultipleDevices((Boolean) config.get("allowMultipleDevices"));
                        vo.setAutoLogoutEnabled((Boolean) config.get("autoLogoutEnabled"));
                        vo.setRememberMeDays((Integer) config.get("rememberMeDays"));
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to deserialize config", e);
                }
                
                return vo;
            }
        }.execute();
    }
    
    @Override
    public PageResult<ActiveSessionVo> listActiveSessions(ActiveSessionFindDto dto) {
        return new BizTemplate<PageResult<ActiveSessionVo>>() {
            @Override
            protected PageResult<ActiveSessionVo> process() {
                // TODO: Implement list active sessions logic
                List<ActiveSessionVo> vos = new ArrayList<>();
                return PageResult.of(0L, vos);
            }
        }.execute();
    }
    
    @Override
    public PageResult<SecurityEventVo> listSecurityEvents(SecurityEventFindDto dto) {
        return new BizTemplate<PageResult<SecurityEventVo>>() {
            @Override
            protected PageResult<SecurityEventVo> process() {
                Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getSize());
                Page<Security> page = securityRepo.findByType(SecurityType.SECURITY_EVENT, pageable);
                
                List<SecurityEventVo> vos = page.getContent().stream()
                    .map(security -> {
                        SecurityEventVo vo = new SecurityEventVo();
                        vo.setId(security.getId());
                        
                        try {
                            if (security.getConfig() != null) {
                                Map<String, Object> config = objectMapper.readValue(
                                    security.getConfig().toString(), 
                                    new TypeReference<Map<String, Object>>() {}
                                );
                                vo.setType((String) config.get("type"));
                                vo.setLevel((String) config.get("level"));
                                vo.setUserId(((Number) config.get("userId")).longValue());
                                vo.setUserName((String) config.get("userName"));
                                vo.setIpAddress((String) config.get("ipAddress"));
                                vo.setLocation((String) config.get("location"));
                                vo.setDescription((String) config.get("description"));
                                vo.setHandled((Boolean) config.get("handled"));
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to deserialize config", e);
                        }
                        
                        return vo;
                    })
                    .collect(Collectors.toList());
                
                return PageResult.of(page.getTotalElements(), vos);
            }
        }.execute();
    }
    
    @Override
    public SecurityAuditStatsVo getAuditStats(SecurityAuditStatsFindDto dto) {
        return new BizTemplate<SecurityAuditStatsVo>() {
            @Override
            protected SecurityAuditStatsVo process() {
                SecurityAuditStatsVo vo = new SecurityAuditStatsVo();
                
                SecurityAuditStatsVo.Period period = new SecurityAuditStatsVo.Period();
                period.setStartDate(dto.getStartDate());
                period.setEndDate(dto.getEndDate());
                vo.setPeriod(period);
                
                // TODO: Implement audit stats calculation
                vo.setTotalEvents(0L);
                vo.setHighRiskEvents(0L);
                vo.setMediumRiskEvents(0L);
                vo.setLowRiskEvents(0L);
                vo.setLoginFailures(0L);
                vo.setPasswordChanges(0L);
                vo.setPermissionChanges(0L);
                vo.setEventsByDay(new ArrayList<>());
                
                return vo;
            }
        }.execute();
    }
}
