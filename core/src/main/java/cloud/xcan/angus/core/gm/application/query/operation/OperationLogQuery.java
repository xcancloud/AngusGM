package cloud.xcan.angus.core.gm.application.query.operation;

import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface OperationLogQuery {

  Page<OperationLog> list(Specification<OperationLog> spec, Pageable pageable);

}
