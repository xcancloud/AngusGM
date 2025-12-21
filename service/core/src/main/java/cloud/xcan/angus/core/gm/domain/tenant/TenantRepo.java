package cloud.xcan.angus.core.gm.domain.tenant;

import cloud.xcan.angus.core.gm.domain.tenant.enums.TenantStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Tenant repository interface
 */
@NoRepositoryBean
public interface TenantRepo extends BaseRepository<Tenant, Long> {

  /**
   * Check if tenant code exists
   */
  boolean existsByCode(String code);

  /**
   * Check if tenant code exists excluding given id
   */
  boolean existsByCodeAndIdNot(String code, Long id);

  /**
   * Count tenants by status
   */
  long countByStatus(TenantStatus status);

  /**
   * Count tenants created after a given date
   */
  long countByCreatedDateAfter(LocalDateTime date);
}
