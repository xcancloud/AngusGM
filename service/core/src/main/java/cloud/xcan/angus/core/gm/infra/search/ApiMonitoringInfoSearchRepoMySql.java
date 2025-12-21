package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoringInfo;
import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoringInfoSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * API监控信息MySQL搜索仓储实现
 * </p>
 */
@Repository
public class ApiMonitoringInfoSearchRepoMySql extends SimpleSearchRepository<ApiMonitoringInfo> implements
    ApiMonitoringInfoSearchRepo {

}

