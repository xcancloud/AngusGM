package cloud.xcan.angus.core.gm.application.query.operation;

import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface OperationLogQuery {

  Page<OperationLog> list(GenericSpecification<OperationLog> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

}
