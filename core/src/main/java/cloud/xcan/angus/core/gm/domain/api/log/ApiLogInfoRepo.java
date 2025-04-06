package cloud.xcan.angus.core.gm.domain.api.log;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface ApiLogInfoRepo extends BaseRepository<ApiLogInfo, Long> {

}
