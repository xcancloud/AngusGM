package cloud.xcan.angus.core.gm.infra.persistence.postgres.group;

import cloud.xcan.angus.core.gm.domain.group.Group;
import cloud.xcan.angus.core.gm.domain.group.GroupRepo;
import org.springframework.stereotype.Repository;

/**
 * PostgreSQL implementation of GroupRepo
 */
@Repository
public interface GroupRepoPostgres extends GroupRepo {
  // Spring Data JPA will implement methods automatically
}
