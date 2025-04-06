package cloud.xcan.angus.core.gm.domain.email;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface EmailRepo extends BaseRepository<Email, Long> {

  long countByOutId(String outId);

  /**
   * Configuration 2, retry only once after first failure.
   */
  @Query(value = "select * from email where expected_send_date <= ?1 and send_status <> 'SUCCESS' and send_retry_num < 2", nativeQuery = true)
  List<Email> findAllInPending(LocalDateTime now, Pageable pageable);

  /**
   * Fix:: Condition receive_object_type <> 'ALL' does not contain null value -> receive_object_type
   * <> 'ALL' OR receive_object_type is null
   */
  @Query(value = "SELECT * FROM email WHERE send_status = 'PENDING' AND (receive_object_type <> 'ALL' OR receive_object_type is null) LIMIT ?1", nativeQuery = true)
  List<Email> findTenantEmailInPending(int count);

  @Query(value = "SELECT * FROM email WHERE send_status = 'PENDING' AND receive_object_type = 'ALL' LIMIT ?1", nativeQuery = true)
  List<Email> findPlatformEmailInPending(int count);

  @Modifying
  @Query(value = "DELETE FROM email WHERE id in ?1", nativeQuery = true)
  void deleteByIdIn(HashSet<Long> ids);

}
