package cloud.xcan.angus.core.gm.infra.persistence.mysql.group;

import cloud.xcan.angus.api.commonlink.user.group.GroupUserRepo;
import org.springframework.stereotype.Repository;


@Repository("groupUserRepo")
public interface GroupUserRepoMySql extends GroupUserRepo {

}
