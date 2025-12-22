package cloud.xcan.angus.core.gm.application.cmd.quota.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.quota.QuotaCmd;
import cloud.xcan.angus.core.gm.application.query.quota.QuotaQuery;
import cloud.xcan.angus.core.gm.domain.quota.Quota;
import cloud.xcan.angus.core.gm.domain.quota.QuotaRepo;
import cloud.xcan.angus.core.gm.domain.quota.QuotaStatus;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>Implementation of quota command service</p>
 */
@Biz
public class QuotaCmdImpl implements QuotaCmd {
    
    @Resource
    private QuotaRepo quotaRepo;
    
    @Resource
    private QuotaQuery quotaQuery;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Quota create(Quota quota) {
        return new BizTemplate<Quota>() {
            @Override
            protected void checkParams() {
                // Add parameter validation if needed
            }

            @Override
            protected Quota process() {
                quota.setStatus(QuotaStatus.ACTIVE);
                return quotaRepo.save(quota);
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Quota update(Quota quota) {
        return new BizTemplate<Quota>() {
            Quota quotaDb;

            @Override
            protected void checkParams() {
                quotaDb = quotaQuery.findById(quota.getId())
                    .orElseThrow(() -> ResourceNotFound.of("配额未找到", new Object[]{}));
            }

            @Override
            protected Quota process() {
                // Update fields from quota to quotaDb
                if (quota.getName() != null) {
                    quotaDb.setName(quota.getName());
                }
                if (quota.getStatus() != null) {
                    quotaDb.setStatus(quota.getStatus());
                }
                if (quota.getLimitValue() != null) {
                    quotaDb.setLimitValue(quota.getLimitValue());
                }
                if (quota.getWarningThreshold() != null) {
                    quotaDb.setWarningThreshold(quota.getWarningThreshold());
                }
                if (quota.getEnabled() != null) {
                    quotaDb.setEnabled(quota.getEnabled());
                }
                if (quota.getDescription() != null) {
                    quotaDb.setDescription(quota.getDescription());
                }
                return quotaRepo.save(quotaDb);
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        new BizTemplate<Void>() {
            @Override
            protected void checkParams() {
                quotaQuery.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("配额未找到", new Object[]{}));
            }

            @Override
            protected Void process() {
                quotaRepo.deleteById(id);
                return null;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(Long id) {
        new BizTemplate<Void>() {
            Quota quotaDb;

            @Override
            protected void checkParams() {
                quotaDb = quotaQuery.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("配额未找到", new Object[]{}));
            }

            @Override
            protected Void process() {
                quotaDb.setEnabled(true);
                quotaRepo.save(quotaDb);
                return null;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disable(Long id) {
        new BizTemplate<Void>() {
            Quota quotaDb;

            @Override
            protected void checkParams() {
                quotaDb = quotaQuery.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("配额未找到", new Object[]{}));
            }

            @Override
            protected Void process() {
                quotaDb.setEnabled(false);
                quotaRepo.save(quotaDb);
                return null;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseUsage(Long id, Long amount) {
        new BizTemplate<Void>() {
            Quota quotaDb;

            @Override
            protected void checkParams() {
                quotaDb = quotaQuery.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("配额未找到", new Object[]{}));
                if (amount == null || amount <= 0) {
                    throw new IllegalArgumentException("增加的使用量必须大于0");
                }
            }

            @Override
            protected Void process() {
                quotaDb.setUsedValue(quotaDb.getUsedValue() + amount);
                if (quotaDb.getUsedValue() >= quotaDb.getLimitValue()) {
                    quotaDb.setStatus(QuotaStatus.EXCEEDED);
                } else if (quotaDb.getUsedValue() >= quotaDb.getWarningThreshold()) {
                    quotaDb.setStatus(QuotaStatus.WARNING);
                }
                quotaRepo.save(quotaDb);
                return null;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decreaseUsage(Long id, Long amount) {
        new BizTemplate<Void>() {
            Quota quotaDb;

            @Override
            protected void checkParams() {
                quotaDb = quotaQuery.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("配额未找到", new Object[]{}));
                if (amount == null || amount <= 0) {
                    throw new IllegalArgumentException("减少的使用量必须大于0");
                }
            }

            @Override
            protected Void process() {
                quotaDb.setUsedValue(Math.max(0, quotaDb.getUsedValue() - amount));
                // Update status based on usage
                if (quotaDb.getUsedValue() >= quotaDb.getLimitValue()) {
                    quotaDb.setStatus(QuotaStatus.EXCEEDED);
                } else if (quotaDb.getUsedValue() >= quotaDb.getWarningThreshold()) {
                    quotaDb.setStatus(QuotaStatus.WARNING);
                } else {
                    quotaDb.setStatus(QuotaStatus.ACTIVE);
                }
                quotaRepo.save(quotaDb);
                return null;
            }
        }.execute();
    }
}
