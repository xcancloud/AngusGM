package cloud.xcan.angus.core.gm.domain.operation;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface OperationLogRepo extends BaseRepository<OperationLog, Long> {

  @Modifying
  @Query(value = "DELETE FROM operation_log WHERE opt_date <= ?1", nativeQuery = true)
  void deleteByOptDateBefore(LocalDateTime delDate);

}
