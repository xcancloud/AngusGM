package cloud.xcan.angus.core.gm.application.query.operation.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.operation.OperationLogQuery;
import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import cloud.xcan.angus.core.gm.domain.operation.OperationLogRepo;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


@Slf4j
@Biz
@SummaryQueryRegister(name = "OperationLog", table = "operation_log",
    groupByColumns = {"opt_date", "success"})
public class OperationLogQueryImpl implements OperationLogQuery {

  @Resource
  private OperationLogRepo optionLogRepo;

  @Override
  public Page<OperationLog> list(Specification<OperationLog> spec, Pageable pageable) {
    return new BizTemplate<Page<OperationLog>>(true, true) {

      @Override
      protected Page<OperationLog> process() {
        return optionLogRepo.findAll(spec, pageable);
      }
    }.execute();
  }

}
