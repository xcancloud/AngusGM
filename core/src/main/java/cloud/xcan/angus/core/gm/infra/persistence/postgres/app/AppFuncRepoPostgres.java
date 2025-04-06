package cloud.xcan.angus.core.gm.infra.persistence.postgres.app;

import cloud.xcan.angus.core.gm.domain.app.func.AppFuncRepo;
import org.springframework.stereotype.Repository;


@Repository("appFuncRepo")
public interface AppFuncRepoPostgres extends AppFuncRepo {

}
