package cloud.xcan.angus.core.gm.domain.message;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface MessageRepo extends BaseRepository<Message, Long> {

  List<Message> findAllByReceiveTypeAndStatusAndTimingDateBefore(MessageReceiveType receiveType,
      MessageStatus status, LocalDateTime now, Pageable pageable);

  @Modifying
  @Query(value = "UPDATE Message SET readNum = readNum + 1 WHERE id IN ?1")
  void incrReadNum(Collection<Long> messageIds);

  @Modifying
  @Query(value = "UPDATE Message SET deleted=true , deletedDate = ?2 WHERE id IN ?1")
  void updateDeletedByIdIn(Collection<Long> ids, LocalDateTime now);
}
