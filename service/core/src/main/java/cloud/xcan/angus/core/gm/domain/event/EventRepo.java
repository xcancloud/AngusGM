package cloud.xcan.angus.core.gm.domain.event;

import cloud.xcan.angus.core.gm.domain.event.push.EventPushStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface EventRepo extends BaseRepository<Event, Long> {

  @Query(value = "SELECT * FROM event WHERE push_status = ?1 limit ?2", nativeQuery = true)
  List<Event> findAllByPushStatus(String pushStatus, int size);

  long countByIdAndPushStatus(Long id, EventPushStatus status);

  @Modifying
  @Query(value = "UPDATE event SET push_status=?2, push_msg=?3 WHERE id=?1 AND push_status <> 'PUSH_SUCCESS'", nativeQuery = true)
  void updatePushStatusWhenNotSuccess(Long id, String pushStatus, String pushMsg);

  @Modifying
  @Query(value = "UPDATE event SET push_status=?2, push_msg=?3 WHERE id in ?1 AND push_status <> 'PUSH_SUCCESS'", nativeQuery = true)
  void updatePushStatusWhenNotSuccess(Collection<Long> ids, String pushStatus, String pushMsg);
}
