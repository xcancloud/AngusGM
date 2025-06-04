package cloud.xcan.angus.core.gm.application.query.operation.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isTenantClient;
import static cloud.xcan.angus.spec.experimental.BizConstant.XCAN_TENANT_PLATFORM_CODE;

import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.operation.OperationLogSearch;
import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import cloud.xcan.angus.core.gm.domain.operation.OperationLogSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Biz
public class OperationLogSearchImpl implements OperationLogSearch {

  @Resource
  private OperationLogSearchRepo operationLogSearchRepo;

  @Resource
  private UserManager userManager;

  @Override
  public Page<OperationLog> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<OperationLog> clz, String... matches) {
    return new BizTemplate<Page<OperationLog>>() {

      @Override
      protected Page<OperationLog> process() {
        if (isTenantClient()) {
          criteria.add(SearchCriteria.equal("clientId", XCAN_TENANT_PLATFORM_CODE));
        }

        Page<OperationLog> page = operationLogSearchRepo.find(criteria, pageable, clz, matches);
        if (page.hasContent()) {
          userManager.setUserNameAndAvatar(page.getContent(), "userId", "fullName", "avatar");
        }
        return page;
      }
    }.execute();
  }

}
