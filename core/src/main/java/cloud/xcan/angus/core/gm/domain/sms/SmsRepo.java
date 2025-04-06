package cloud.xcan.angus.core.gm.domain.sms;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface SmsRepo extends BaseRepository<Sms, Long> {

  @Query(value = "SELECT * FROM sms WHERE expected_send_date <= ?1 AND send_status <> 'SUCCESS' "
      + "AND send_retry_num <= 3", nativeQuery = true)
  List<Sms> findAllInPending(LocalDateTime now, Pageable pageable);

  @Modifying
  @Query("DELETE FROM Sms s WHERE s.id IN (?1)")
  void deleteByIdIn(HashSet<Long> ids);

  @Query(value = "SELECT * FROM sms WHERE send_status = ?1 LIMIT ?2", nativeQuery = true)
  List<Sms> findAllBySendStatusAndSize(String sendStatus, int count);
}
