package cloud.xcan.angus.core.gm.infra.persistence.mysql.app;

import cloud.xcan.angus.api.commonlink.app.AppInfoRepo;
import org.springframework.stereotype.Repository;


@Repository("appInfoRepo")
public interface AppInfoRepoMySql extends AppInfoRepo {

}
