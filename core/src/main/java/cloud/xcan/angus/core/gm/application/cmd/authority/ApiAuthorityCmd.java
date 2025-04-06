package cloud.xcan.angus.core.gm.application.cmd.authority;

import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.authority.ApiAuthoritySource;
import java.util.List;
import java.util.Set;

public interface ApiAuthorityCmd {

  void replaceAppApiAuthority(App app);

  void saveAppApiAuthority(App app);

  void updateAppAuthorityStatus(List<App> apps, Set<Long> ids);

  void updateAuthorityServiceStatus(Set<Long> serviceIds, List<Service> services);

  void deleteBySource(Set<Long> sourceId, ApiAuthoritySource source);
}
