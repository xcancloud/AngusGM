package cloud.xcan.angus.core.gm.application.cmd.api;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.Collection;
import java.util.List;
import org.springframework.cloud.client.ServiceInstance;

public interface ApiCmd {

  List<IdKey<Long, Object>> add(List<Api> apis);

  void update(List<Api> apis);

  List<IdKey<Long, Object>> replace(List<Api> apis);

  void delete(Collection<Long> ids);

  void enabled(List<Api> apis);

  void syncServiceApi(ServiceInstance instance, Service serviceDb);

  void discoveryApiSync(List<ServiceInstance> instances, List<Service> servicesDb,
      List<String> discoveryServices);

  List<Api> parseApisFromSwagger(ServiceInstance instance, Service service);

  void updateApiServiceStatus(List<Service> services);

  void saveSyncApis(String serviceCode, List<Api> apis);

}
