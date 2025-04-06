package cloud.xcan.angus.core.gm.infra.persistence.postgres.group;

import cloud.xcan.angus.api.commonlink.group.GroupRepo;
import org.springframework.stereotype.Repository;


@Repository("groupRepo")
public interface GroupRepoPostgres extends GroupRepo {

}
