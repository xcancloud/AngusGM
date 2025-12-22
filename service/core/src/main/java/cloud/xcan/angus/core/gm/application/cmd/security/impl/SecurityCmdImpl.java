package cloud.xcan.angus.core.gm.application.cmd.security.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.security.SecurityCmd;
import cloud.xcan.angus.core.gm.application.query.security.SecurityQuery;
import cloud.xcan.angus.core.gm.domain.security.Security;
import cloud.xcan.angus.core.gm.domain.security.SecurityRepo;
import cloud.xcan.angus.core.gm.domain.security.SecurityType;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.*;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Implementation of security command service</p>
 */
@Biz
public class SecurityCmdImpl implements SecurityCmd {
    
    @Resource
    private SecurityRepo securityRepo;
    
    @Resource
    private SecurityQuery securityQuery;
    
    @Resource
    private ObjectMapper objectMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PasswordPolicyVo updatePasswordPolicy(PasswordPolicyUpdateDto dto) {
        return new BizTemplate<PasswordPolicyVo>() {
            Security securityDb;
            
            @Override
            protected void checkParams() {
                securityDb = securityRepo.findByTypeAndScope(SecurityType.PASSWORD_POLICY, "DEFAULT")
                    .orElse(null);
            }
            
            @Override
            protected PasswordPolicyVo process() {
                if (securityDb == null) {
                    securityDb = new Security();
                    securityDb.setName("密码策略");
                    securityDb.setType(SecurityType.PASSWORD_POLICY);
                    securityDb.setScope("DEFAULT");
                }
                
                Map<String, Object> config = new HashMap<>();
                if (dto.getMinLength() != null) config.put("minLength", dto.getMinLength());
                if (dto.getMaxLength() != null) config.put("maxLength", dto.getMaxLength());
                if (dto.getRequireUppercase() != null) config.put("requireUppercase", dto.getRequireUppercase());
                if (dto.getRequireLowercase() != null) config.put("requireLowercase", dto.getRequireLowercase());
                if (dto.getRequireNumbers() != null) config.put("requireNumbers", dto.getRequireNumbers());
                if (dto.getRequireSpecialChars() != null) config.put("requireSpecialChars", dto.getRequireSpecialChars());
                if (dto.getPreventReuse() != null) config.put("preventReuse", dto.getPreventReuse());
                if (dto.getExpirationDays() != null) config.put("expirationDays", dto.getExpirationDays());
                if (dto.getWarningDays() != null) config.put("warningDays", dto.getWarningDays());
                if (dto.getMaxLoginAttempts() != null) config.put("maxLoginAttempts", dto.getMaxLoginAttempts());
                if (dto.getLockoutDuration() != null) config.put("lockoutDuration", dto.getLockoutDuration());
                
                try {
                    securityDb.setConfig(objectMapper.writeValueAsString(config));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to serialize config", e);
                }
                
                securityDb.setEnabled(true);
                Security saved = securityRepo.save(securityDb);
                
                PasswordPolicyVo vo = new PasswordPolicyVo();
                vo.setId(saved.getId());
                vo.setMinLength(dto.getMinLength());
                vo.setMaxLength(dto.getMaxLength());
                vo.setRequireUppercase(dto.getRequireUppercase());
                vo.setRequireLowercase(dto.getRequireLowercase());
                vo.setRequireNumbers(dto.getRequireNumbers());
                vo.setRequireSpecialChars(dto.getRequireSpecialChars());
                vo.setPreventReuse(dto.getPreventReuse());
                vo.setExpirationDays(dto.getExpirationDays());
                vo.setWarningDays(dto.getWarningDays());
                vo.setMaxLoginAttempts(dto.getMaxLoginAttempts());
                vo.setLockoutDuration(dto.getLockoutDuration());
                vo.setIsEnabled(true);
                
                return vo;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TwoFactorConfigVo updateTwoFactorConfig(TwoFactorConfigUpdateDto dto) {
        return new BizTemplate<TwoFactorConfigVo>() {
            Security securityDb;
            
            @Override
            protected void checkParams() {
                securityDb = securityRepo.findByTypeAndScope(SecurityType.TWO_FACTOR, "DEFAULT")
                    .orElse(null);
            }
            
            @Override
            protected TwoFactorConfigVo process() {
                if (securityDb == null) {
                    securityDb = new Security();
                    securityDb.setName("双因素认证");
                    securityDb.setType(SecurityType.TWO_FACTOR);
                    securityDb.setScope("DEFAULT");
                }
                
                Map<String, Object> config = new HashMap<>();
                if (dto.getIsEnabled() != null) config.put("isEnabled", dto.getIsEnabled());
                if (dto.getMethods() != null) config.put("methods", dto.getMethods());
                if (dto.getDefaultMethod() != null) config.put("defaultMethod", dto.getDefaultMethod());
                if (dto.getCodeExpiration() != null) config.put("codeExpiration", dto.getCodeExpiration());
                if (dto.getTrustDeviceDays() != null) config.put("trustDeviceDays", dto.getTrustDeviceDays());
                if (dto.getEnforceForAdmins() != null) config.put("enforceForAdmins", dto.getEnforceForAdmins());
                if (dto.getEnforceForAllUsers() != null) config.put("enforceForAllUsers", dto.getEnforceForAllUsers());
                
                try {
                    securityDb.setConfig(objectMapper.writeValueAsString(config));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to serialize config", e);
                }
                
                securityDb.setEnabled(dto.getIsEnabled() != null ? dto.getIsEnabled() : true);
                Security saved = securityRepo.save(securityDb);
                
                TwoFactorConfigVo vo = new TwoFactorConfigVo();
                vo.setId(saved.getId());
                vo.setIsEnabled(dto.getIsEnabled());
                vo.setMethods(dto.getMethods());
                vo.setDefaultMethod(dto.getDefaultMethod());
                vo.setCodeExpiration(dto.getCodeExpiration());
                vo.setCodeLength(6);
                vo.setTrustDeviceDays(dto.getTrustDeviceDays());
                vo.setEnforceForAdmins(dto.getEnforceForAdmins());
                vo.setEnforceForAllUsers(dto.getEnforceForAllUsers());
                
                return vo;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public IpWhitelistVo addIpWhitelist(IpWhitelistCreateDto dto) {
        return new BizTemplate<IpWhitelistVo>() {
            @Override
            protected void checkParams() {
                // Add validation if needed
            }
            
            @Override
            protected IpWhitelistVo process() {
                Security security = new Security();
                security.setName("IP白名单");
                security.setType(SecurityType.IP_WHITELIST);
                security.setScope("DEFAULT");
                
                Map<String, Object> config = new HashMap<>();
                config.put("ipAddress", dto.getIpAddress());
                config.put("ipRange", dto.getIpRange());
                config.put("description", dto.getDescription());
                config.put("status", dto.getStatus());
                config.put("lastUsed", null);
                config.put("usageCount", 0L);
                
                try {
                    security.setConfig(objectMapper.writeValueAsString(config));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to serialize config", e);
                }
                
                security.setEnabled("启用".equals(dto.getStatus()));
                Security saved = securityRepo.save(security);
                
                IpWhitelistVo vo = new IpWhitelistVo();
                vo.setId(saved.getId());
                vo.setIpAddress(dto.getIpAddress());
                vo.setIpRange(dto.getIpRange());
                vo.setDescription(dto.getDescription());
                vo.setStatus(dto.getStatus());
                vo.setUsageCount(0L);
                
                return vo;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public IpWhitelistVo updateIpWhitelist(Long id, IpWhitelistUpdateDto dto) {
        return new BizTemplate<IpWhitelistVo>() {
            Security securityDb;
            
            @Override
            protected void checkParams() {
                securityDb = securityRepo.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("IP白名单未找到", new Object[]{}));
            }
            
            @Override
            protected IpWhitelistVo process() {
                Map<String, Object> config = new HashMap<>();
                if (dto.getIpAddress() != null) config.put("ipAddress", dto.getIpAddress());
                if (dto.getIpRange() != null) config.put("ipRange", dto.getIpRange());
                if (dto.getDescription() != null) config.put("description", dto.getDescription());
                if (dto.getStatus() != null) config.put("status", dto.getStatus());
                
                try {
                    securityDb.setConfig(objectMapper.writeValueAsString(config));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to serialize config", e);
                }
                
                securityDb.setEnabled("启用".equals(dto.getStatus()));
                Security saved = securityRepo.save(securityDb);
                
                IpWhitelistVo vo = new IpWhitelistVo();
                vo.setId(saved.getId());
                vo.setIpAddress(dto.getIpAddress());
                vo.setIpRange(dto.getIpRange());
                vo.setDescription(dto.getDescription());
                vo.setStatus(dto.getStatus());
                
                return vo;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteIpWhitelist(Long id) {
        new BizTemplate<Void>() {
            @Override
            protected void checkParams() {
                securityRepo.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("IP白名单未找到", new Object[]{}));
            }
            
            @Override
            protected Void process() {
                securityRepo.deleteById(id);
                return null;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SessionConfigVo updateSessionConfig(SessionConfigUpdateDto dto) {
        return new BizTemplate<SessionConfigVo>() {
            Security securityDb;
            
            @Override
            protected void checkParams() {
                securityDb = securityRepo.findByTypeAndScope(SecurityType.SESSION_CONFIG, "DEFAULT")
                    .orElse(null);
            }
            
            @Override
            protected SessionConfigVo process() {
                if (securityDb == null) {
                    securityDb = new Security();
                    securityDb.setName("会话配置");
                    securityDb.setType(SecurityType.SESSION_CONFIG);
                    securityDb.setScope("DEFAULT");
                }
                
                Map<String, Object> config = new HashMap<>();
                if (dto.getTimeout() != null) config.put("timeout", dto.getTimeout());
                if (dto.getMaxConcurrent() != null) config.put("maxConcurrent", dto.getMaxConcurrent());
                if (dto.getAllowMultipleDevices() != null) config.put("allowMultipleDevices", dto.getAllowMultipleDevices());
                if (dto.getAutoLogoutEnabled() != null) config.put("autoLogoutEnabled", dto.getAutoLogoutEnabled());
                if (dto.getRememberMeDays() != null) config.put("rememberMeDays", dto.getRememberMeDays());
                
                try {
                    securityDb.setConfig(objectMapper.writeValueAsString(config));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to serialize config", e);
                }
                
                securityDb.setEnabled(true);
                Security saved = securityRepo.save(securityDb);
                
                SessionConfigVo vo = new SessionConfigVo();
                vo.setId(saved.getId());
                vo.setTimeout(dto.getTimeout());
                vo.setMaxConcurrent(dto.getMaxConcurrent());
                vo.setAllowMultipleDevices(dto.getAllowMultipleDevices());
                vo.setAutoLogoutEnabled(dto.getAutoLogoutEnabled());
                vo.setRememberMeDays(dto.getRememberMeDays());
                
                return vo;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void terminateSession(String sessionId) {
        new BizTemplate<Void>() {
            @Override
            protected void checkParams() {
                // TODO: Validate session exists
            }
            
            @Override
            protected Void process() {
                // TODO: Implement terminate session logic
                return null;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SecurityEventHandleVo handleSecurityEvent(Long id, SecurityEventHandleDto dto) {
        return new BizTemplate<SecurityEventHandleVo>() {
            Security securityDb;
            
            @Override
            protected void checkParams() {
                securityDb = securityRepo.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("安全事件未找到", new Object[]{}));
            }
            
            @Override
            protected SecurityEventHandleVo process() {
                Map<String, Object> config = new HashMap<>();
                config.put("handled", true);
                config.put("handledNote", dto.getHandledNote());
                config.put("handledTime", LocalDateTime.now());
                
                try {
                    securityDb.setConfig(objectMapper.writeValueAsString(config));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to serialize config", e);
                }
                
                Security saved = securityRepo.save(securityDb);
                
                SecurityEventHandleVo vo = new SecurityEventHandleVo();
                vo.setId(saved.getId());
                vo.setHandled(true);
                vo.setHandledNote(dto.getHandledNote());
                vo.setHandledTime(LocalDateTime.now());
                
                return vo;
            }
        }.execute();
    }
}
