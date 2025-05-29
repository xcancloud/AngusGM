package cloud.xcan.angus.core.gm.domain.service;

import cloud.xcan.angus.api.commonlink.service.ServiceResourceApi;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface ServiceResourceApiRepo extends BaseRepository<ServiceResourceApi, Long> {

  @Query(value =
      "SELECT DISTINCT resource_name, resource_description, id api_id, "
          + " name api_name, operation_id api_code, enabled api_enabled, description api_description "
          + " FROM api WHERE service_code = ?1", nativeQuery = true)
  List<ServiceResourceApi> findServiceResourceApiList(String serviceCode);

  @Query(value =
      "SELECT DISTINCT resource_name, resource_description, id api_id, "
          + " name api_name, operation_id api_code, enabled api_enabled, description api_description "
          + " FROM api WHERE service_code = ?1 and resource_name = ?2 ", nativeQuery = true)
  List<ServiceResourceApi> findServiceResourceApiList(String serviceCode, String resourceName);

}
