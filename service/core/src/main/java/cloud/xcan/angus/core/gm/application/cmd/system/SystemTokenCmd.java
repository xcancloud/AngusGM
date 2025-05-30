package cloud.xcan.angus.core.gm.application.cmd.system;

import cloud.xcan.angus.api.commonlink.service.ServiceResource;
import cloud.xcan.angus.core.gm.domain.system.SystemToken;
import cloud.xcan.angus.core.gm.domain.system.resource.SystemTokenResource;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;


public interface SystemTokenCmd {

  SystemToken add(SystemToken systemToken, List<SystemTokenResource> resources);

  @Transactional(rollbackFor = Exception.class)
  CustomOAuth2RegisteredClient saveCustomOAuth2RegisteredClient(SystemToken systemToken);

  @Transactional(rollbackFor = Exception.class)
  void saveSystemAccessToken(SystemToken systemToken, List<SystemTokenResource> resources,
      Map<String, List<ServiceResource>> serviceResourceMap, Map<String, String> result);

  Void delete(HashSet<Long> ids);

  void deleteByApiIdIn(Collection<Long> ids);

}
