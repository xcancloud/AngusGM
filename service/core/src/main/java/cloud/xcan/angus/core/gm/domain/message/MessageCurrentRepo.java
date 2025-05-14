package cloud.xcan.angus.core.gm.domain.message;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface MessageCurrentRepo extends BaseRepository<MessageSent, Long> {

  Optional<MessageSent> findByIdAndReceiveUserId(Long id, Long userId);

  List<MessageSent> getAllByReceiveUserIdAndRead(Long userid, Boolean read);

  long countByReceiveUserIdAndRead(Long userid, Boolean read);

  @Modifying
  @Query(value = "UPDATE message_sent SET deleted = true , deleted_date = ?2 WHERE id IN ?1", nativeQuery = true)
  void updateDeletedByIdIn(Collection<Long> ids, LocalDateTime now);

  @Modifying
  @Query(value = "UPDATE message_sent SET read = true , read_date = ?2 WHERE id IN ?1", nativeQuery = true)
  void updateReadByIdIn(Collection<Long> ids, LocalDateTime now);

}
