package cloud.xcan.angus.core.gm.application.cmd.service.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.service.ServiceCmd;
import cloud.xcan.angus.core.gm.application.query.service.ServiceQuery;
import cloud.xcan.angus.core.gm.domain.security.Security;
import cloud.xcan.angus.core.gm.domain.security.SecurityRepo;
import cloud.xcan.angus.core.gm.domain.security.SecurityType;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.*;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Implementation of service command service</p>
 */
@Biz
public class ServiceCmdImpl implements ServiceCmd {
    
    @Resource
    private SecurityRepo securityRepo;
    
    @Resource
    private ServiceQuery serviceQuery;
    
    @Resource
    private ObjectMapper objectMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceRefreshVo refresh() {
        return new BizTemplate<ServiceRefreshVo>() {
            @Override
            protected void checkParams() {
                // Add validation if needed
            }
            
            @Override
            protected ServiceRefreshVo process() {
                // TODO: Implement refresh from Eureka
                ServiceRefreshVo vo = new ServiceRefreshVo();
                vo.setRefreshTime(LocalDateTime.now());
                vo.setTotalServices(0);
                vo.setTotalInstances(0);
                return vo;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceInstanceStatusVo updateInstanceStatus(String serviceName, String instanceId, ServiceInstanceStatusDto dto) {
        return new BizTemplate<ServiceInstanceStatusVo>() {
            @Override
            protected void checkParams() {
                // Validate service and instance exist
                // TODO: Add validation logic
            }
            
            @Override
            protected ServiceInstanceStatusVo process() {
                // TODO: Implement instance status update via Eureka API
                ServiceInstanceStatusVo vo = new ServiceInstanceStatusVo();
                vo.setInstanceId(instanceId);
                vo.setStatus(dto.getStatus());
                vo.setModifiedDate(LocalDateTime.now());
                return vo;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EurekaConfigVo updateEurekaConfig(EurekaConfigUpdateDto dto) {
        return new BizTemplate<EurekaConfigVo>() {
            Security securityDb;
            
            @Override
            protected void checkParams() {
                securityDb = securityRepo.findByTypeAndScope(SecurityType.EUREKA_CONFIG, "DEFAULT")
                    .orElse(null);
            }
            
            @Override
            protected EurekaConfigVo process() {
                if (securityDb == null) {
                    securityDb = new Security();
                    securityDb.setName("Eureka配置");
                    securityDb.setType(SecurityType.EUREKA_CONFIG);
                    securityDb.setScope("DEFAULT");
                }
                
                Map<String, Object> config = new HashMap<>();
                if (dto.getServiceUrl() != null) config.put("serviceUrl", dto.getServiceUrl());
                if (dto.getEnableAuth() != null) config.put("enableAuth", dto.getEnableAuth());
                if (dto.getUsername() != null) config.put("username", dto.getUsername());
                if (dto.getPassword() != null) config.put("password", dto.getPassword());
                if (dto.getSyncInterval() != null) config.put("syncInterval", dto.getSyncInterval());
                if (dto.getEnableSsl() != null) config.put("enableSsl", dto.getEnableSsl());
                if (dto.getConnectTimeout() != null) config.put("connectTimeout", dto.getConnectTimeout());
                if (dto.getReadTimeout() != null) config.put("readTimeout", dto.getReadTimeout());
                
                try {
                    securityDb.setConfig(objectMapper.writeValueAsString(config));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to serialize config", e);
                }
                
                securityDb.setEnabled(true);
                Security saved = securityRepo.save(securityDb);
                
                EurekaConfigVo vo = new EurekaConfigVo();
                vo.setServiceUrl(dto.getServiceUrl());
                vo.setEnableAuth(dto.getEnableAuth());
                vo.setUsername(dto.getUsername());
                vo.setPassword("******");
                vo.setSyncInterval(dto.getSyncInterval());
                vo.setEnableSsl(dto.getEnableSsl());
                vo.setConnectTimeout(dto.getConnectTimeout());
                vo.setReadTimeout(dto.getReadTimeout());
                vo.setModifiedDate(LocalDateTime.now());
                
                return vo;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EurekaTestVo testEurekaConnection(EurekaTestDto dto) {
        return new BizTemplate<EurekaTestVo>() {
            @Override
            protected void checkParams() {
                if (dto.getServiceUrl() == null || dto.getServiceUrl().isEmpty()) {
                    throw new IllegalArgumentException("Eureka服务URL不能为空");
                }
            }
            
            @Override
            protected EurekaTestVo process() {
                // TODO: Implement Eureka connection test
                EurekaTestVo vo = new EurekaTestVo();
                vo.setConnected(true);
                vo.setResponseTime(100);
                vo.setServicesCount(0);
                return vo;
            }
        }.execute();
    }
}
