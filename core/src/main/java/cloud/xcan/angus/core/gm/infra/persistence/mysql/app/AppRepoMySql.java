package cloud.xcan.angus.core.gm.infra.persistence.mysql.app;

import cloud.xcan.angus.core.gm.domain.app.AppRepo;
import org.springframework.stereotype.Repository;


@Repository("appRepo")
public interface AppRepoMySql extends AppRepo {

}
