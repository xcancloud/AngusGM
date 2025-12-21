package cloud.xcan.angus.core.gm.domain.department;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Department full-text search repository interface
 */
@NoRepositoryBean
public interface DepartmentSearchRepo extends CustomBaseRepository<Department> {

}
