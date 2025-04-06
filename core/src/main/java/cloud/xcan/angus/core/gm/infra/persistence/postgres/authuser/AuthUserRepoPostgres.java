package cloud.xcan.angus.core.gm.infra.persistence.postgres.authuser;

import cloud.xcan.angus.api.commonlink.authuser.AuthUserRepo;
import org.springframework.stereotype.Repository;


@Repository("authUserRepo")
public interface AuthUserRepoPostgres extends AuthUserRepo {


}
