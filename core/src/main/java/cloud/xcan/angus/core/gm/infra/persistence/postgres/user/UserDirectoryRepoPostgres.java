package cloud.xcan.angus.core.gm.infra.persistence.postgres.user;

import cloud.xcan.angus.core.gm.domain.user.directory.UserDirectoryRepo;
import org.springframework.stereotype.Repository;


@Repository("userDirectoryRepo")
public interface UserDirectoryRepoPostgres extends UserDirectoryRepo {

}
