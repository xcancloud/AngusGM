package cloud.xcan.angus.core.gm.infra.persistence.mysql.user;

import cloud.xcan.angus.core.gm.domain.user.directory.UserDirectoryRepo;
import org.springframework.stereotype.Repository;


@Repository("userDirectoryRepo")
public interface UserDirectoryRepoMySql extends UserDirectoryRepo {

}
