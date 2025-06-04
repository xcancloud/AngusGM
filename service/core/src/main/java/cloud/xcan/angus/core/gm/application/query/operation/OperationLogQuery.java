package cloud.xcan.angus.core.gm.application.query.operation;

import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface OperationLogQuery {

  Page<OperationLog> list(GenericSpecification<OperationLog> spec, Pageable pageable);

}
