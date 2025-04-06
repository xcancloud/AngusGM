package cloud.xcan.angus.core.gm.application.query.api;

import cloud.xcan.angus.core.gm.domain.api.log.ApiLog;
import cloud.xcan.angus.core.gm.domain.api.log.ApiLogInfo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ApiLogsQuery {

  ApiLog detail(Long id);

  Page<ApiLogInfo> list(GenericSpecification<ApiLogInfo> spec, Pageable pageable);

  void joinApiInfo(List<ApiLog> apiLogs);
}
