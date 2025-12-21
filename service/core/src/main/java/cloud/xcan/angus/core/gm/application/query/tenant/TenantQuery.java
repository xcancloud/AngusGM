package cloud.xcan.angus.core.gm.application.query.tenant;

import cloud.xcan.angus.core.gm.domain.tenant.Tenant;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Tenant query service interface
 */
public interface TenantQuery {

  /**
   * Find tenant by ID with validation
   */
  Tenant findAndCheck(Long id);

  /**
   * Find tenants with pagination and full-text search support
   */
  Page<Tenant> find(GenericSpecification<Tenant> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  /**
   * Get tenant statistics
   */
  TenantStatsVo getStats();
}
