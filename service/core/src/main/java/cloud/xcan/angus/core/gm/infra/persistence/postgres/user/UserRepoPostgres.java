package cloud.xcan.angus.core.gm.infra.persistence.postgres.user;

import cloud.xcan.angus.core.gm.domain.user.User;
import cloud.xcan.angus.core.gm.domain.user.UserRepo;
import org.springframework.stereotype.Repository;

/**
 * PostgreSQL implementation of UserRepo
 */
@Repository
public interface UserRepoPostgres extends UserRepo {
  // Spring Data JPA will implement methods automatically
}
