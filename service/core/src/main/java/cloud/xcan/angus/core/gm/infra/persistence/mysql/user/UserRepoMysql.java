package cloud.xcan.angus.core.gm.infra.persistence.mysql.user;

import cloud.xcan.angus.core.gm.domain.user.User;
import cloud.xcan.angus.core.gm.domain.user.UserRepo;
import org.springframework.stereotype.Repository;

/**
 * MySQL implementation of UserRepo
 */
@Repository
public interface UserRepoMysql extends UserRepo {
  // Spring Data JPA will implement methods automatically
}
