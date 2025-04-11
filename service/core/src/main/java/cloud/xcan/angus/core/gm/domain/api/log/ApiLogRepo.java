package cloud.xcan.angus.core.gm.domain.api.log;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface ApiLogRepo extends BaseRepository<ApiLog, Long> {

  @Modifying
  @Query(value = "DELETE FROM api_log WHERE request_date <= ?1", nativeQuery = true)
  void deleteByRequestDateBefore(LocalDateTime delDate);
}
