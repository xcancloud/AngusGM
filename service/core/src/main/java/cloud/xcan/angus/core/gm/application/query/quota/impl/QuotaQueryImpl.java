package cloud.xcan.angus.core.gm.application.query.quota.impl;

import cloud.xcan.angus.core.gm.application.query.quota.QuotaQuery;
import cloud.xcan.angus.core.gm.domain.quota.Quota;
import cloud.xcan.angus.core.gm.domain.quota.QuotaRepo;
import cloud.xcan.angus.core.gm.domain.quota.QuotaStatus;
import cloud.xcan.angus.core.gm.domain.quota.QuotaType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuotaQueryImpl implements QuotaQuery {
    
    private final QuotaRepo quotaRepo;
    
    @Override
    public Optional<Quota> findById(Long id) {
        return quotaRepo.findById(id);
    }
    
    @Override
    public Optional<Quota> findByName(String name) {
        return quotaRepo.findByName(name);
    }
    
    @Override
    public List<Quota> findByType(QuotaType type) {
        return quotaRepo.findByType(type);
    }
    
    @Override
    public List<Quota> findByStatus(QuotaStatus status) {
        return quotaRepo.findByStatus(status);
    }
    
    @Override
    public List<Quota> findByTenantId(Long tenantId) {
        return quotaRepo.findByTenantId(tenantId);
    }
    
    @Override
    public List<Quota> findAll() {
        return quotaRepo.findAll();
    }
}
