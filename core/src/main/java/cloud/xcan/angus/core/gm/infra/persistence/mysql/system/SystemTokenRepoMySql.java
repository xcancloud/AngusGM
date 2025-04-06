package cloud.xcan.angus.core.gm.infra.persistence.mysql.system;

import cloud.xcan.angus.core.gm.domain.system.SystemTokenRepo;
import org.springframework.stereotype.Repository;


@Repository("systemTokenRepo")
public interface SystemTokenRepoMySql extends SystemTokenRepo {

}
