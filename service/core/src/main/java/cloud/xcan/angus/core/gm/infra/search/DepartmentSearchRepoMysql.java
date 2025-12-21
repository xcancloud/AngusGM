package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.department.Department;
import cloud.xcan.angus.core.gm.domain.department.DepartmentSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Department full-text search repository MySQL implementation
 */
@Repository
public class DepartmentSearchRepoMysql extends SimpleSearchRepository<Department>
    implements DepartmentSearchRepo {

}
