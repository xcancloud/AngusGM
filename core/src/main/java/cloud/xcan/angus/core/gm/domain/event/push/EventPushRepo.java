package cloud.xcan.angus.core.gm.domain.event.push;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface EventPushRepo extends BaseRepository<EventPush, Long> {

  @Query(value = "SELECT * FROM event_push WHERE push = 0 AND retry_times <= ?2 LIMIT ?1", nativeQuery = true)
  List<EventPush> findPushEventInPending(int size, int maxRetryNum);

}
