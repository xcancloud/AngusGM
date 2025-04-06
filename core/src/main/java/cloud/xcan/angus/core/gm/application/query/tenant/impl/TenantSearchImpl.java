package cloud.xcan.angus.core.gm.application.query.tenant.impl;

import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantSearch;
import cloud.xcan.angus.core.gm.domain.tenant.TenantSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Biz
public class TenantSearchImpl implements TenantSearch {

  @Resource
  private TenantSearchRepo tenantSearchRepo;

  @Resource
  private TenantQuery tenantQuery;

  @Override
  public Page<Tenant> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<Tenant> clz, String... matches) {
    return new BizTemplate<Page<Tenant>>() {

      @Override
      protected Page<Tenant> process() {
        Page<Tenant> page = tenantSearchRepo.find(criteria, pageable, clz, matches);
        tenantQuery.setUserCount(page.getContent());
        return page;
      }
    }.execute();
  }

}
