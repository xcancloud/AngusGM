package cloud.xcan.angus.core.gm.domain.system;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface SystemTokenRepo extends BaseRepository<SystemToken, Long> {

  boolean existsByName(String name);

  long countByTenantId(Long tenantId);

  @Modifying
  @Query(value = "DELETE FROM system_token WHERE id IN ?1", nativeQuery = true)
  void deleteByIdIn(Collection<Long> ids);
}
