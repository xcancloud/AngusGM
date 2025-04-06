package cloud.xcan.angus.core.gm.infra.persistence.mysql.app;

import cloud.xcan.angus.api.commonlink.app.open.AppAuthRepo;
import org.springframework.stereotype.Repository;

@Repository("appAuthRepo")
public interface AppAuthRepoMysql extends AppAuthRepo {

}
