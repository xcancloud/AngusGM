package cloud.xcan.angus.core.gm.infra.persistence.mysql.group;

import cloud.xcan.angus.core.gm.domain.group.Group;
import cloud.xcan.angus.core.gm.domain.group.GroupRepo;
import org.springframework.stereotype.Repository;

/**
 * MySQL implementation of GroupRepo
 */
@Repository
public interface GroupRepoMysql extends GroupRepo {
  // Spring Data JPA will implement methods automatically
}
