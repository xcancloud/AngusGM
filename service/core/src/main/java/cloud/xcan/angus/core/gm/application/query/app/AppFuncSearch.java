package cloud.xcan.angus.core.gm.application.query.app;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.List;
import java.util.Set;

public interface AppFuncSearch {

  List<AppFunc> search(Set<SearchCriteria> criteria, Class<AppFunc> clz, String... matches);
}
