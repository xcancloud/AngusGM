package cloud.xcan.angus.core.gm.application.query.operation.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isTenantClient;
import static cloud.xcan.angus.spec.experimental.BizConstant.XCAN_TENANT_PLATFORM_CODE;

import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.operation.OperationLogQuery;
import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import cloud.xcan.angus.core.gm.domain.operation.OperationLogRepo;
import cloud.xcan.angus.core.gm.domain.operation.OperationLogSearchRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * <p>
 * Implementation of operation log query operations.
 * </p>
 * <p>
 * Manages operation log retrieval, filtering, and user information association.
 * Provides comprehensive operation log querying with full-text search and summary support.
 * </p>
 * <p>
 * Supports operation log listing with client filtering and user information enrichment
 * for comprehensive operation log management.
 * </p>
 */
@Slf4j
@Biz
@SummaryQueryRegister(name = "OperationLog", table = "operation_log",
    groupByColumns = {"opt_date", "resource"})
public class OperationLogQueryImpl implements OperationLogQuery {

  @Resource
  private OperationLogRepo optionLogRepo;
  @Resource
  private OperationLogSearchRepo operationLogSearchRepo;
  @Resource
  private UserManager userManager;

  /**
   * <p>
   * Retrieves operation logs with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search and specification-based filtering.
   * Applies client filtering for tenant clients and enriches results with user information.
   * </p>
   */
  @Override
  public Page<OperationLog> list(GenericSpecification<OperationLog> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<OperationLog>>(true, true) {

      @Override
      protected Page<OperationLog> process() {
        if (isTenantClient()) {
          spec.add(SearchCriteria.equal("clientId", XCAN_TENANT_PLATFORM_CODE));
        }
        Page<OperationLog> page = fullTextSearch
            ? operationLogSearchRepo.find(spec.getCriteria(), pageable, OperationLog.class, match)
            : optionLogRepo.findAll(spec, pageable);
        if (page.hasContent()) {
          userManager.setUserNameAndAvatar(page.getContent(), "userId", "fullName", "avatar");
        }
        return page;
      }
    }.execute();
  }
}
