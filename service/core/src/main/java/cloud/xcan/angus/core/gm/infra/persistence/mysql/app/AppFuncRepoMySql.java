package cloud.xcan.angus.core.gm.infra.persistence.mysql.app;

import cloud.xcan.angus.core.gm.domain.app.func.AppFuncRepo;
import org.springframework.stereotype.Repository;


@Repository("appFuncRepo")
public interface AppFuncRepoMySql extends AppFuncRepo {

}
