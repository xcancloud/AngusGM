package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.user.User;
import cloud.xcan.angus.core.gm.domain.user.UserSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

/**
 * User full-text search repository MySQL implementation
 */
@Repository
public class UserSearchRepoMysql extends SimpleSearchRepository<User>
    implements UserSearchRepo {

}
