package cloud.xcan.angus.core.gm.application.cmd.service;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;

public interface ServiceCmd {

  IdKey<Long, Object> add(Service service);

  void update(Service service);

  IdKey<Long, Object> replace(Service service);

  void delete(HashSet<Long> ids);

  void enabled(List<Service> services);

  List<IdKey<Long, Object>> apiAdd(List<Api> apis);

  void apiDelete(Long serviceId, HashSet<Long> apiIds);

  void syncServiceApi(String serviceCode);

  void discoveryApiSync();
}
