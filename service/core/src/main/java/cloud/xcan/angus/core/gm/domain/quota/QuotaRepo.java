package cloud.xcan.angus.core.gm.domain.quota;

import cloud.xcan.angus.core.gm.domain.BaseRepo;

import java.util.List;
import java.util.Optional;

/**
 * <p>Quota repository interface</p>
 */
public interface QuotaRepo extends BaseRepo<Quota> {
    
    /**
     * <p>Save quota entity</p>
     */
    Quota save(Quota quota);
    
    /**
     * <p>Delete quota by id</p>
     */
    void deleteById(Long id);
    
    /**
     * <p>Find quota by name</p>
     */
    Optional<Quota> findByName(String name);
    
    /**
     * <p>Find quotas by type</p>
     */
    List<Quota> findByType(QuotaType type);
    
    /**
     * <p>Find quotas by status</p>
     */
    List<Quota> findByStatus(QuotaStatus status);
    
    /**
     * <p>Find quotas by tenant id</p>
     */
    List<Quota> findByTenantId(Long tenantId);
    
    /**
     * <p>Count quotas by type</p>
     */
    long countByType(QuotaType type);
}
