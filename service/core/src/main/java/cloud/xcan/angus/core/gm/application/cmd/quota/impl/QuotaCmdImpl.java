package cloud.xcan.angus.core.gm.application.cmd.quota.impl;

import cloud.xcan.angus.core.gm.application.cmd.quota.QuotaCmd;
import cloud.xcan.angus.core.gm.domain.quota.Quota;
import cloud.xcan.angus.core.gm.domain.quota.QuotaRepo;
import cloud.xcan.angus.core.gm.domain.quota.QuotaStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuotaCmdImpl implements QuotaCmd {
    
    private final QuotaRepo quotaRepo;
    
    @Override
    @Transactional
    public Quota create(Quota quota) {
        quota.setStatus(QuotaStatus.ACTIVE);
        return quotaRepo.save(quota);
    }
    
    @Override
    @Transactional
    public Quota update(Quota quota) {
        return quotaRepo.save(quota);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        quotaRepo.deleteById(id);
    }
    
    @Override
    @Transactional
    public void enable(Long id) {
        Quota quota = quotaRepo.findById(id).orElseThrow();
        quota.setEnabled(true);
        quotaRepo.save(quota);
    }
    
    @Override
    @Transactional
    public void disable(Long id) {
        Quota quota = quotaRepo.findById(id).orElseThrow();
        quota.setEnabled(false);
        quotaRepo.save(quota);
    }
    
    @Override
    @Transactional
    public void increaseUsage(Long id, Long amount) {
        Quota quota = quotaRepo.findById(id).orElseThrow();
        quota.setUsedValue(quota.getUsedValue() + amount);
        if (quota.getUsedValue() >= quota.getLimitValue()) {
            quota.setStatus(QuotaStatus.EXCEEDED);
        }
        quotaRepo.save(quota);
    }
    
    @Override
    @Transactional
    public void decreaseUsage(Long id, Long amount) {
        Quota quota = quotaRepo.findById(id).orElseThrow();
        quota.setUsedValue(Math.max(0, quota.getUsedValue() - amount));
        quotaRepo.save(quota);
    }
}
