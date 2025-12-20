package cloud.xcan.angus.core.gm.application.cmd.authorization.impl;

import cloud.xcan.angus.core.gm.application.cmd.authorization.AuthorizationCmd;
import cloud.xcan.angus.core.gm.domain.authorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRepo;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationStatus;
import cloud.xcan.angus.share.exception.BizAssert;
import cloud.xcan.angus.share.template.BizTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Authorization Command Service Implementation
 */
@Service
public class AuthorizationCmdImpl implements AuthorizationCmd {
    
    @Resource
    private AuthorizationRepo authorizationRepo;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Authorization create(Authorization authorization) {
        return BizTemplate.execute(() -> {
            // Set default status
            if (authorization.getStatus() == null) {
                authorization.setStatus(AuthorizationStatus.ENABLED);
            }
            
            // Save authorization
            return authorizationRepo.save(authorization);
        });
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Authorization update(Authorization authorization) {
        return BizTemplate.execute(() -> {
            // Check if authorization exists
            Authorization existing = authorizationRepo.findById(authorization.getId()).orElse(null);
            BizAssert.notNull(existing, "Authorization not found");
            
            // Update authorization
            return authorizationRepo.save(authorization);
        });
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(Long id) {
        BizTemplate.execute(() -> {
            Authorization authorization = authorizationRepo.findById(id).orElse(null);
            BizAssert.notNull(authorization, "Authorization not found");
            
            authorization.setStatus(AuthorizationStatus.ENABLED);
            authorizationRepo.save(authorization);
            return null;
        });
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disable(Long id) {
        BizTemplate.execute(() -> {
            Authorization authorization = authorizationRepo.findById(id).orElse(null);
            BizAssert.notNull(authorization, "Authorization not found");
            
            authorization.setStatus(AuthorizationStatus.DISABLED);
            authorizationRepo.save(authorization);
            return null;
        });
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        BizTemplate.execute(() -> {
            Authorization authorization = authorizationRepo.findById(id).orElse(null);
            BizAssert.notNull(authorization, "Authorization not found");
            
            authorizationRepo.delete(authorization);
            return null;
        });
    }
}
