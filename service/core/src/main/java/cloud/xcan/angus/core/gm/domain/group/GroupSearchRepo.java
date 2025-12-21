package cloud.xcan.angus.core.gm.domain.group;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Group search repository for full-text search
 */
@NoRepositoryBean
public interface GroupSearchRepo extends CustomBaseRepository<Group> {
}
