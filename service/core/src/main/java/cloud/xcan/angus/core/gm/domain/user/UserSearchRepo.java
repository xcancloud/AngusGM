package cloud.xcan.angus.core.gm.domain.user;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * User full-text search repository interface
 */
@NoRepositoryBean
public interface UserSearchRepo extends CustomBaseRepository<User> {

}
