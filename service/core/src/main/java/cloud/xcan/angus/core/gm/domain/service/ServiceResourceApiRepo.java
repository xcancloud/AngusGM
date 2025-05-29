package cloud.xcan.angus.core.gm.domain.service;

import cloud.xcan.angus.api.commonlink.service.ServiceResourceApi;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface ServiceResourceApiRepo extends BaseRepository<ServiceResourceApi, Long> {

  @Query(value =
      "SELECT DISTINCT resource_name resourceName, resource_description resourceDesc, id apiId, "
          + " name apiName, code apiCode, enabled apiEnabled, description apiDescription "
          + " FROM api WHERE service_code = ?1", nativeQuery = true)
  List<ServiceResourceApi> findServiceResourceApiList(String serviceCode);

  @Query(value =
      "SELECT DISTINCT resource_name resourceName, resource_description resourceDesc, id apiId, "
          + " name apiName, code apiCode, enabled apiEnabled, description apiDescription "
          + " FROM api WHERE service_code = ?1 and resource_name = ?2 ", nativeQuery = true)
  List<ServiceResourceApi> findServiceResourceApiList(String serviceCode, String resourceName);

}
