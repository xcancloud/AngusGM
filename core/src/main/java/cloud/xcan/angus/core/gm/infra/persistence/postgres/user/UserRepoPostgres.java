package cloud.xcan.angus.core.gm.infra.persistence.postgres.user;

import cloud.xcan.angus.api.commonlink.user.UserRepo;
import org.springframework.stereotype.Repository;


@Repository("userRepo")
public interface UserRepoPostgres extends UserRepo {

}
