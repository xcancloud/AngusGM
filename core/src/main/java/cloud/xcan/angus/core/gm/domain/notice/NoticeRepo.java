package cloud.xcan.angus.core.gm.domain.notice;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface NoticeRepo extends BaseRepository<Notice, Long> {

  Notice findFirstByScopeOrderByIdDesc(NoticeScope scope);

  Notice findFirstByAppIdAndScopeOrderByIdDesc(Long appId, NoticeScope scope);

  @Modifying
  @Query(value = "DELETE FROM notice WHERE id IN (?1)", nativeQuery = true)
  void deleteByIdIn(Collection<Long> ids);
}
