package cloud.xcan.angus.core.gm.domain.message;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface MessageSentRepo extends BaseRepository<MessageSent, Long> {

  @Query(value = "SELECT a0.receive_user_id FROM message_sent a0 GROUP BY a0.receive_user_id HAVING(count(a0.receive_user_id) > ?1) LIMIT ?2", nativeQuery = true)
  List<Long> getReceiveUserIdHavingCount(Long reservedNum, Long batchNum);

  @Modifying
  @Query(value = "DELETE FROM message_sent WHERE receive_user_id = ?1 AND id NOT IN "
      + "(SELECT id FROM (SELECT id FROM message_sent WHERE receive_user_id = ?1 ORDER BY id DESC LIMIT ?2) as a)", nativeQuery = true)
  void deleteByReceiveUserIdAndCount(Long targetId, Long reservedNum);
}
