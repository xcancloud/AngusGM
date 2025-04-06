package cloud.xcan.angus.api.commonlink.api;

import cloud.xcan.angus.api.commonlink.service.ServiceResource;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository("commonApiRepo")
public interface ApiRepo extends BaseRepository<Api, Long> {

  List<Api> findByIdIn(Collection<Long> ids);

  List<Api> findAllByIdInAndEnabled(Collection<Long> ids, Boolean enabled);

  List<Api> findAllByServiceIdAndIdIn(Long serviceId, HashSet<Long> apiIds);

  List<Api> findAllByServiceCode(String serviceCode);

  List<Api> findAllByServiceCodeIn(Collection<String> serviceCodes);

  List<Api> findAllByServiceId(Long serviceId);

  @Query(value = "SELECT DISTINCT service_code serviceCode, resource_name resourceName, resource_desc resourceDesc FROM api", nativeQuery = true)
  List<ServiceResource> findServiceResource();

  @Query(value = "SELECT DISTINCT service_code serviceCode, resource_name resourceName, resource_desc resourceDesc FROM api WHERE service_code = ?1", nativeQuery = true)
  List<ServiceResource> findServiceResourceByServiceCode(String serviceCode);

  @Query(value = "SELECT DISTINCT service_code serviceCode, resource_name resourceName, resource_desc resourceDesc FROM api WHERE resource_name IN ?1", nativeQuery = true)
  List<ServiceResource> findServiceResourceByResourceNameIn(Collection<String> resourceNames);

  @Query(value = "SELECT a.id FROM Api a WHERE a.serviceId IN ?1")
  List<Long> findIdByServiceId(Long serviceId);

  @Modifying
  @Query(value = "UPDATE api SET service_code = ?2 WHERE service_id = ?1", nativeQuery = true)
  int updateServiceCodeByServiceId(Long serviceId, String serviceCode);

  @Modifying
  @Query(value = "UPDATE api SET service_name = ?2 WHERE service_id = ?1", nativeQuery = true)
  int updateServiceNameByServiceId(Long serviceId, String serviceName);

  @Modifying
  @Query(value = "update api SET service_enabled = ?2 WHERE service_id IN ?1", nativeQuery = true)
  void updateServiceEnabled(Collection<Long> enabledServiceIds, boolean enabled);

  @Modifying
  @Query(value = "DELETE FROM api WHERE service_code = ?1 AND id IN ?2", nativeQuery = true)
  void deleteByServiceCodeAndIdIn(String serviceCode, HashSet<Long> ids);

  @Modifying
  @Query(value = "DELETE FROM api WHERE id IN ?1", nativeQuery = true)
  void deleteByIdIn(Collection<Long> ids);

}
