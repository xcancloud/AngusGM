package cloud.xcan.angus.core.gm.application.query.quota;

import cloud.xcan.angus.core.gm.domain.quota.Quota;
import cloud.xcan.angus.core.gm.domain.quota.QuotaStatus;
import cloud.xcan.angus.core.gm.domain.quota.QuotaType;

import java.util.List;
import java.util.Optional;

public interface QuotaQuery {
    Optional<Quota> findById(Long id);
    Optional<Quota> findByName(String name);
    List<Quota> findByType(QuotaType type);
    List<Quota> findByStatus(QuotaStatus status);
    List<Quota> findByTenantId(Long tenantId);
    List<Quota> findAll();
}
