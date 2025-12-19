package cloud.xcan.angus.core.gm.application.query.tenant.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.domain.tenant.Tenant;
import cloud.xcan.angus.core.gm.domain.tenant.TenantRepo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Implementation of tenant query service
 */
@Biz
public class TenantQueryImpl implements TenantQuery {

  @Resource
  private TenantRepo tenantRepo;

  @Override
  public Tenant findAndCheck(Long id) {
    return new BizTemplate<Tenant>() {
      @Override
      protected Tenant process() {
        Tenant tenant = tenantRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("租户未找到", new Object[]{}));
        
        // Set associated data
        // TODO: Set userCount and departmentCount from respective repositories
        tenant.setUserCount(0L);
        tenant.setDepartmentCount(0L);
        
        return tenant;
      }
    }.execute();
  }

  @Override
  public Page<Tenant> find(GenericSpecification<Tenant> spec, PageRequest pageable) {
    return new BizTemplate<Page<Tenant>>() {
      @Override
      protected Page<Tenant> process() {
        Page<Tenant> page = tenantRepo.findAll(spec, pageable);
        
        // Set associated data for each tenant
        if (page.hasContent()) {
          page.getContent().forEach(tenant -> {
            // TODO: Batch query for userCount and departmentCount
            tenant.setUserCount(0L);
            tenant.setDepartmentCount(0L);
          });
        }
        
        return page;
      }
    }.execute();
  }

  @Override
  public TenantStatsVo getStats() {
    return new BizTemplate<TenantStatsVo>() {
      @Override
      protected TenantStatsVo process() {
        TenantStatsVo stats = new TenantStatsVo();
        
        // Get total tenants
        long totalTenants = tenantRepo.count();
        stats.setTotalTenants(totalTenants);
        
        // Get enabled tenants
        long enabledTenants = tenantRepo.countByStatus("已启用");
        stats.setEnabledTenants(enabledTenants);
        
        // Get disabled tenants
        long disabledTenants = tenantRepo.countByStatus("已禁用");
        stats.setDisabledTenants(disabledTenants);
        
        // TODO: Get total users from user repository
        stats.setTotalUsers(0L);
        
        // TODO: Get new tenants this month
        stats.setNewTenantsThisMonth(0L);
        
        // Calculate growth rate
        if (totalTenants > 0) {
          stats.setGrowthRate(0.0);
        } else {
          stats.setGrowthRate(0.0);
        }
        
        return stats;
      }
    }.execute();
  }
}
