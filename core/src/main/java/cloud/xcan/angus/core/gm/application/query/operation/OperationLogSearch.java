package cloud.xcan.angus.core.gm.application.query.operation;

import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OperationLogSearch {

  Page<OperationLog> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<OperationLog> clz, String... matches);
}
