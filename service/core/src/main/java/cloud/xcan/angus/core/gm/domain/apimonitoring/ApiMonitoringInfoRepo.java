package cloud.xcan.angus.core.gm.domain.apimonitoring;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * <p>
 * API监控信息仓储接口（用于查询）
 * </p>
 */
@NoRepositoryBean
public interface ApiMonitoringInfoRepo extends BaseRepository<ApiMonitoringInfo, Long> {

}

