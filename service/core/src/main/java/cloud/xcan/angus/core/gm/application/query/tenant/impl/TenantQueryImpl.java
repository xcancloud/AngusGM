package cloud.xcan.angus.core.gm.application.query.tenant.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.domain.tenant.Tenant;
import cloud.xcan.angus.core.gm.domain.tenant.TenantRepo;
import cloud.xcan.angus.core.gm.domain.tenant.TenantSearchRepo;
import cloud.xcan.angus.core.gm.domain.tenant.enums.TenantStatus;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Implementation of tenant query service
 */
@Biz
public class TenantQueryImpl implements TenantQuery {

  @Resource
  private TenantRepo tenantRepo;

  @Resource
  private TenantSearchRepo tenantSearchRepo;

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
  public Page<Tenant> find(GenericSpecification<Tenant> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Tenant>>() {
      @Override
      protected Page<Tenant> process() {
        Page<Tenant> page = fullTextSearch
            ? tenantSearchRepo.find(spec.getCriteria(), pageable, Tenant.class, match)
            : tenantRepo.findAll(spec, pageable);
        setAssociatedData(page);
        return page;
      }
    }.execute();
  }

  private void setAssociatedData(Page<Tenant> page) {
    if (page.hasContent()) {
      page.getContent().forEach(tenant -> {
        // TODO: Batch query for userCount and departmentCount
        tenant.setUserCount(0L);
        tenant.setDepartmentCount(0L);
      });
    }
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
        long enabledTenants = tenantRepo.countByStatus(TenantStatus.ENABLED);
        stats.setEnabledTenants(enabledTenants);
        
        // Get disabled tenants
        long disabledTenants = tenantRepo.countByStatus(TenantStatus.DISABLED);
        stats.setDisabledTenants(disabledTenants);
        
        // TODO: Get total users from user repository
        stats.setTotalUsers(0L);
        
        // Get new tenants this month
        LocalDateTime firstDayOfMonth = LocalDateTime.now()
            .with(TemporalAdjusters.firstDayOfMonth())
            .withHour(0).withMinute(0).withSecond(0).withNano(0);
        long newTenantsThisMonth = tenantRepo.countByCreatedDateAfter(firstDayOfMonth);
        stats.setNewTenantsThisMonth(newTenantsThisMonth);
        
        // Calculate growth rate
        long existingTenants = totalTenants - newTenantsThisMonth;
        if (existingTenants > 0) {
          double growthRate = (newTenantsThisMonth * 100.0) / existingTenants;
          stats.setGrowthRate(Math.round(growthRate * 10.0) / 10.0);
        } else {
          stats.setGrowthRate(0.0);
        }
        
        return stats;
      }
    }.execute();
  }
}
