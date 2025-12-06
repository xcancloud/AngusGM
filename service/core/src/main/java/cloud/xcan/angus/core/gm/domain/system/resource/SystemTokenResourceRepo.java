package cloud.xcan.angus.core.gm.domain.system.resource;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface SystemTokenResourceRepo extends BaseRepository<SystemTokenResource, Long> {

  List<SystemTokenResource> findBySystemTokenId(Long id);

  @Modifying
  @Query(value = "DELETE FROM system_token_resource WHERE system_token_id IN ?1", nativeQuery = true)
  void deleteBySystemTokenIdIn(Collection<Long> ids);

  void deleteByResourceIdIn(Collection<Long> resourceIds);

}
