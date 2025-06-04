package cloud.xcan.angus.core.gm.application.query.operation.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isTenantClient;
import static cloud.xcan.angus.spec.experimental.BizConstant.XCAN_TENANT_PLATFORM_CODE;

import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.operation.OperationLogQuery;
import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import cloud.xcan.angus.core.gm.domain.operation.OperationLogRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


@Slf4j
@Biz
@SummaryQueryRegister(name = "OperationLog", table = "operation_log",
    groupByColumns = {"opt_date", "resource"})
public class OperationLogQueryImpl implements OperationLogQuery {

  @Resource
  private OperationLogRepo optionLogRepo;

  @Resource
  private UserManager userManager;

  @Override
  public Page<OperationLog> list(GenericSpecification<OperationLog> spec, Pageable pageable) {
    return new BizTemplate<Page<OperationLog>>(true, true) {

      @Override
      protected Page<OperationLog> process() {
        if (isTenantClient()) {
          spec.add(SearchCriteria.equal("clientId", XCAN_TENANT_PLATFORM_CODE));
        }
        Page<OperationLog> page = optionLogRepo.findAll(spec, pageable);
        if (page.hasContent()) {
          userManager.setUserNameAndAvatar(page.getContent(), "userId", "fullName", "avatar");
        }
        return page;
      }
    }.execute();
  }

}
