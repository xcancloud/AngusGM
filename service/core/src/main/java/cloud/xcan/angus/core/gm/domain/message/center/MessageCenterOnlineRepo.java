package cloud.xcan.angus.core.gm.domain.message.center;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MessageCenterOnlineRepo extends BaseRepository<MessageCenterOnline, Long> {

  @Override
  Page<MessageCenterOnline> findAll(Specification<MessageCenterOnline> spec, Pageable pageable);

  List<MessageCenterOnline> findAllByUserIdIn(Collection<Long> userIds);

  @Modifying
  @Query(value = "UPDATE message_center_online SET online = false, offline_date = now() WHERE session_id = ?1 AND user_id = ?2", nativeQuery = true)
  void updateOfflineBySessionIdAndUserId(String sessionId, Long userId);

  @Modifying
  @Query(value = "UPDATE message_center_online SET online = false, offline_date = now() WHERE user_id IN ?1", nativeQuery = true)
  void updateOfflineByUserIdIn(Collection<Long> userIds);

}
