package cloud.xcan.angus.core.gm.domain.apimonitoring;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * <p>
 * API监控信息搜索仓储接口（用于全文搜索）
 * </p>
 */
@NoRepositoryBean
public interface ApiMonitoringInfoSearchRepo extends CustomBaseRepository<ApiMonitoringInfo> {

}

