package cloud.xcan.angus.api.manager;

import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryBuilder;
import java.util.List;
import java.util.Map;

public interface SimpleSummaryManager {

  Object getSummary(SummaryQueryBuilder builder);

  Map<String, Object> getSummary(List<SummaryQueryBuilder> builders);

}
