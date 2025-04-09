package cloud.xcan.angus.core.gm.application.query.operation.impl;

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

  @Override
  public Page<OperationLog> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<OperationLog> clz, String... matches) {
    return new BizTemplate<Page<OperationLog>>() {

      @Override
      protected Page<OperationLog> process() {
        return operationLogSearchRepo.find(criteria, pageable, clz, matches);
      }
    }.execute();
  }

}
