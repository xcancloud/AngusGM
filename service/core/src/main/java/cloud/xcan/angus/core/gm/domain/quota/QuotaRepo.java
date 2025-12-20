package cloud.xcan.angus.core.gm.domain.quota;

import cloud.xcan.angus.core.gm.domain.BaseRepo;

import java.util.List;
import java.util.Optional;

/**
 * 配额仓储接口
 */
public interface QuotaRepo extends BaseRepo<Quota> {
    
    Optional<Quota> findByName(String name);
    
    List<Quota> findByType(QuotaType type);
    
    List<Quota> findByStatus(QuotaStatus status);
    
    List<Quota> findByTenantId(Long tenantId);
    
    long countByType(QuotaType type);
}
