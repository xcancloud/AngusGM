package cloud.xcan.angus.core.gm.application.query.tenant.impl;


import static cloud.xcan.angus.api.commonlink.UCConstant.BID_TENANT;
import static cloud.xcan.angus.api.commonlink.UCConstant.TENANT_SIGN_CANCEL_LOCK_MS;
import static cloud.xcan.angus.api.commonlink.UCConstant.TOP_TENANT_ADMIN;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertForbidden;
import static cloud.xcan.angus.core.gm.domain.UCCoreMessage.TENANT_IS_CANCELED;
import static cloud.xcan.angus.core.spring.SpringContextHolder.getBidGenerator;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;

import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.tenant.TenantRepo;
import cloud.xcan.angus.api.commonlink.user.UserBase;
import cloud.xcan.angus.api.commonlink.user.UserBaseRepo;
import cloud.xcan.angus.api.enums.TenantStatus;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.SneakyThrow0;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.jpa.repository.LongKeyCountSummary;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.security.model.AccountNotFoundException;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


@Biz
@SummaryQueryRegister(name = "Tenant", table = "tenant", isMultiTenantCtrl = false, topAuthority = TOP_TENANT_ADMIN,
    groupByColumns = {"created_date", "source", "type", "status", "real_name_status",
        "locked"}, aggregateColumns = {"id", "user_count"})
public class TenantQueryImpl implements TenantQuery {

  @Resource
  private TenantRepo tenantRepo;

  @Resource
  private UserBaseRepo userBaseRepo;

  @Override
  public Tenant detail(Long id) {
    return new BizTemplate<Tenant>() {
      Tenant tenantDb;

      @SneakyThrow0
      @Override
      protected void checkParams() {
        tenantDb = checkAndFind(id);
      }

      @Override
      protected Tenant process() {
        setUserCount(List.of(tenantDb));
        return tenantDb;
      }
    }.execute();
  }

  @Override
  public List<Tenant> findAllById(Set<Long> ids) {
    return new BizTemplate<List<Tenant>>() {
      @Override
      protected List<Tenant> process() {
        return tenantRepo.findAllById(ids);
      }
    }.execute();
  }

  @Override
  public Page<Tenant> find(Specification<Tenant> spec, Pageable pageable) {
    return new BizTemplate<Page<Tenant>>() {
      @Override
      protected Page<Tenant> process() {
        Page<Tenant> page = tenantRepo.findAll(spec, pageable);
        setUserCount(page.getContent());
        return page;
      }
    }.execute();
  }

  @Override
  public Tenant find0(Long id) {
    return tenantRepo.findById(id).orElse(null);
  }

  @Override
  public Tenant checkAndFind(Long id) {
    return tenantRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Tenant"));
  }

  @Override
  public void checkTenantStatus(String tenantId) {
    // Check and find tenant
    Tenant tenant = find0(Long.valueOf(tenantId));
    if (isNull(tenant)) {
      throw new AccountNotFoundException("Tenant account is not found");
    }

    // Check the tenant status is valid
    if (TenantStatus.CANCELED.equals(tenant.getStatus())) {
      throw new AccountNotFoundException("Tenant account is cancelled");
    }
    if (TenantStatus.DISABLED.equals(tenant.getStatus())) {
      throw new AccountNotFoundException("Tenant account is disabled");
    }
    if (tenant.getLocked()) {
      throw new AccountNotFoundException("Tenant account is locked");
    }
  }

  @Override
  public Set<Long> findCancelExpired(Long count) {
    return tenantRepo.findCancelExpiredTenant(LocalDateTime.now()
        .plusSeconds(-TENANT_SIGN_CANCEL_LOCK_MS), count);
  }

  @Override
  public Set<Long> findLockExpire(Long count) {
    return tenantRepo.findLockExpire(LocalDateTime.now(), count);
  }

  @Override
  public Set<Long> findUnlockExpire(Long count) {
    return tenantRepo.findUnockExpire(LocalDateTime.now(), count);
  }

  @Override
  public void checkTenantCanceled(Tenant tenantDb) {
    assertForbidden(!tenantDb.getStatus().equals(TenantStatus.CANCELED), TENANT_IS_CANCELED);
  }

  @Override
  public void setUserCount(List<Tenant> tenants) {
    if (isNotEmpty(tenants)) {
      Map<Long, Long> userCountMap = userBaseRepo.countByFiltersAndGroup(UserBase.class,
              LongKeyCountSummary.class, null, "tenantId", "id").stream()
          .collect(Collectors.toMap(LongKeyCountSummary::getKey, LongKeyCountSummary::getTotal));
      for (Tenant tenant : tenants) {
        tenant.setUserCount(userCountMap.containsKey(tenant.getId())
            ? userCountMap.get(tenant.getId()) : 0);
      }
    }
  }

  public static String genTenantNo() {
    return getBidGenerator().getId(BID_TENANT) + RandomStringUtils.randomNumeric(2);
  }
}
