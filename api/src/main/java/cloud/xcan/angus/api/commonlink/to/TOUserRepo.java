package cloud.xcan.angus.api.commonlink.to;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository("commonTOUserRepo")
public interface TOUserRepo extends BaseRepository<TOUser, Long> {

  List<TOUser> findAllByUserIdIn(Collection<Long> userIds);

  Optional<TOUser> findByUserId(Long userId);

  @Modifying
  @Query(value = "DELETE FROM to_user WHERE user_id IN ?1", nativeQuery = true)
  void deleteAllByUserIdIn(Collection<Long> userIds);

}
