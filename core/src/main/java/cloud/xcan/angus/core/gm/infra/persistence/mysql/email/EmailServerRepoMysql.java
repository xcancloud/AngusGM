package cloud.xcan.angus.core.gm.infra.persistence.mysql.email;

import cloud.xcan.angus.core.gm.domain.email.server.EmailServerRepo;
import org.springframework.stereotype.Repository;

@Repository("serverRepo")
public interface EmailServerRepoMysql extends EmailServerRepo {

}
