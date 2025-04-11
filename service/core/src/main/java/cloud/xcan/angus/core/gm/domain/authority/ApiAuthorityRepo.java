package cloud.xcan.angus.core.gm.domain.authority;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ApiAuthorityRepo extends BaseRepository<ApiAuthority, Long> {

  List<ApiAuthority> findByAppIdIn(Collection<Long> apiIds);

  List<ApiAuthority> findByApiIdIn(Collection<Long> apiIds);

  List<ApiAuthority> findByServiceIdIn(Collection<Long> serviceIds);

  @Modifying
  @Query(value = "DELETE FROM auth_api_authority WHERE source_id IN ?1 AND source = ?2", nativeQuery = true)
  void deleteBySourceIdInAndSource(Collection<Long> sourceIds, String source);

  @Modifying
  @Query(value = "DELETE FROM auth_api_authority WHERE api_id IN ?1", nativeQuery = true)
  void deleteByApiIdIn(Collection<Long> apiIds);

  @Modifying
  @Query(value = "DELETE FROM auth_api_authority WHERE service_code IN ?1", nativeQuery = true)
  void deleteByServiceCodeIn(Collection<String> serviceCodes);

}
