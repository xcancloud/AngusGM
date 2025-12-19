package cloud.xcan.angus.core.gm.application.cmd.quota;

import cloud.xcan.angus.core.gm.domain.quota.Quota;

public interface QuotaCmd {
    Quota create(Quota quota);
    Quota update(Quota quota);
    void delete(Long id);
    void enable(Long id);
    void disable(Long id);
    void increaseUsage(Long id, Long amount);
    void decreaseUsage(Long id, Long amount);
}
