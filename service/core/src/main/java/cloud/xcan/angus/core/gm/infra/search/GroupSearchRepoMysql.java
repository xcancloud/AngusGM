package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.group.Group;
import cloud.xcan.angus.core.gm.domain.group.GroupSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

/**
 * MySQL implementation of group search repository
 */
@Repository
public class GroupSearchRepoMysql extends SimpleSearchRepository<Group>
    implements GroupSearchRepo {
}
