package cloud.xcan.angus.core.gm.infra.persistence.postgres.app;

import cloud.xcan.angus.core.gm.domain.app.AppRepo;
import org.springframework.stereotype.Repository;


@Repository("appRepo")
public interface AppRepoPostgres extends AppRepo {

}
