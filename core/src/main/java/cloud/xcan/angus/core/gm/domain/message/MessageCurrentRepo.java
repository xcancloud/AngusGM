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
  @Query(value = "UPDATE MessageSent SET deleted = true , deletedDate = ?2 WHERE id in ?1")
  void updateDeletedByIdIn(Collection<Long> ids, LocalDateTime now);

  @Modifying
  @Query(value = "UPDATE MessageSent SET read = true , readDate = ?2 WHERE id in ?1")
  void updateReadByIdIn(Collection<Long> ids, LocalDateTime now);

}
