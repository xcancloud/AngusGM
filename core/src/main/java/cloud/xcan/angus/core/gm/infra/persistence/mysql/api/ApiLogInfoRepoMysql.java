package cloud.xcan.angus.core.gm.infra.persistence.mysql.api;

import cloud.xcan.angus.core.gm.domain.api.log.ApiLogInfoRepo;
import org.springframework.stereotype.Repository;


@Repository("apiLogInfoRepo")
public interface ApiLogInfoRepoMysql extends ApiLogInfoRepo {

}
