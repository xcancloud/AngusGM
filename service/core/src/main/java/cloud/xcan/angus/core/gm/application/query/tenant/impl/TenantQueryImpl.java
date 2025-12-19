package cloud.xcan.angus.core.gm.application.query.tenant.impl;

import static cloud.xcan.angus.api.commonlink.UCConstant.BID_TENANT;
import static cloud.xcan.angus.api.commonlink.UCConstant.TENANT_SIGN_CANCEL_LOCK_MS;
import static cloud.xcan.angus.api.commonlink.UCConstant.TOP_TENANT_ADMIN;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertForbidden;
import static cloud.xcan.angus.core.gm.domain.UserMessage.TENANT_IS_CANCELED;
import static cloud.xcan.angus.core.spring.SpringContextHolder.getBidGenerator;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;

import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.tenant.TenantRepo;
import cloud.xcan.angus.api.commonlink.user.UserBase;
import cloud.xcan.angus.api.commonlink.user.UserBaseRepo;
import cloud.xcan.angus.api.enums.TenantStatus;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.SneakyThrow0;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.domain.tenant.TenantSearchRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
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
import org.springframework.data.domain.PageRequest;

/**
 * <p>
 * Implementation of tenant query operations.
 * </p>
 * <p>
 * Manages tenant retrieval, validation, and status management. Provides comprehensive tenant
 * querying with full-text search and summary support.
 * </p>
 * <p>
 * Supports tenant detail retrieval, paginated listing, status validation, user count calculation,
 * and lifecycle management for comprehensive tenant administration.
 * </p>
 */
@org.springframework.stereotype.Service
@SummaryQueryRegister(name = "Tenant", table = "tenant", isMultiTenantCtrl = false, topAuthority = TOP_TENANT_ADMIN,
    groupByColumns = {"created_date", "source", "type", "status", "real_name_status",
        "locked"}, aggregateColumns = {"id", "user_count"})
public class TenantQueryImpl implements TenantQuery {

  @Resource
  private TenantRepo tenantRepo;
  @Resource
  private TenantSearchRepo tenantSearchRepo;
  @Resource
  private UserBaseRepo userBaseRepo;

  /**
   * <p>
   * Retrieves detailed tenant information by ID.
   * </p>
   * <p>
   * Fetches complete tenant record with user count calculation. Validates tenant existence and
   * enriches with user statistics.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves tenants by IDs.
   * </p>
   * <p>
   * Returns tenants for the specified tenant IDs. Returns empty list if no tenants found.
   * </p>
   */
  @Override
  public List<Tenant> findAllById(Set<Long> ids) {
    return new BizTemplate<List<Tenant>>() {
      @Override
      protected List<Tenant> process() {
        return tenantRepo.findAllById(ids);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves tenants with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search and specification-based filtering. Returns paginated tenant results
   * with user count calculation.
   * </p>
   */
  @Override
  public Page<Tenant> list(GenericSpecification<Tenant> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Tenant>>() {
      @Override
      protected Page<Tenant> process() {
        Page<Tenant> page = fullTextSearch
            ? tenantSearchRepo.find(spec.getCriteria(), pageable, Tenant.class, match)
            : tenantRepo.findAll(spec, pageable);
        setUserCount(page.getContent());
        return page;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves tenant by ID without validation.
   * </p>
   * <p>
   * Returns tenant without existence validation. Returns null if tenant does not exist.
   * </p>
   */
  @Override
  public Tenant find0(Long id) {
    return tenantRepo.findById(id).orElse(null);
  }

  /**
   * <p>
   * Validates and retrieves tenant by ID.
   * </p>
   * <p>
   * Returns tenant with existence validation. Throws ResourceNotFound if tenant does not exist.
   * </p>
   */
  @Override
  public Tenant checkAndFind(Long id) {
    return tenantRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Tenant"));
  }

  /**
   * <p>
   * Validates tenant status for authentication.
   * </p>
   * <p>
   * Checks tenant existence and validates tenant status for login. Throws AccountNotFoundException
   * for invalid tenant states.
   * </p>
   */
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

  /**
   * <p>
   * Finds tenants with expired cancellation lock.
   * </p>
   * <p>
   * Returns tenant IDs that have exceeded cancellation lock period. Used for cleanup of expired
   * cancellation requests.
   * </p>
   */
  @Override
  public Set<Long> findCancelExpired(Long count) {
    return tenantRepo.findCancelExpiredTenant(LocalDateTime.now()
        .plusSeconds(-TENANT_SIGN_CANCEL_LOCK_MS), count);
  }

  /**
   * <p>
   * Finds tenants with expired lock.
   * </p>
   * <p>
   * Returns tenant IDs that have exceeded lock period. Used for automatic unlock processing.
   * </p>
   */
  @Override
  public Set<Long> findLockExpire(Long count) {
    return tenantRepo.findLockExpire(LocalDateTime.now(), count);
  }

  /**
   * <p>
   * Finds tenants with expired unlock.
   * </p>
   * <p>
   * Returns tenant IDs that have exceeded unlock period. Used for automatic lock processing.
   * </p>
   */
  @Override
  public Set<Long> findUnlockExpire(Long count) {
    return tenantRepo.findUnockExpire(LocalDateTime.now(), count);
  }

  /**
   * <p>
   * Validates tenant is not canceled.
   * </p>
   * <p>
   * Ensures tenant status is not canceled for operations. Throws Forbidden exception if tenant is
   * canceled.
   * </p>
   */
  @Override
  public void checkTenantCanceled(Tenant tenantDb) {
    assertForbidden(!tenantDb.getStatus().equals(TenantStatus.CANCELED), TENANT_IS_CANCELED);
  }

  /**
   * <p>
   * Sets user count for tenant list.
   * </p>
   * <p>
   * Calculates and sets user count for each tenant in the list. Uses database aggregation for
   * efficient user count calculation.
   * </p>
   */
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

  /**
   * <p>
   * Generates unique tenant number.
   * </p>
   * <p>
   * Creates tenant number using bid generator and random numeric suffix. Ensures unique tenant
   * identification across the system.
   * </p>
   */
  public static String genTenantNo() {
    return getBidGenerator().getId(BID_TENANT) + RandomStringUtils.randomNumeric(2);
  }
}
