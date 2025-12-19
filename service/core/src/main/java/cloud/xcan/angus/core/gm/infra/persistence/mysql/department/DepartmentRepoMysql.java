package cloud.xcan.angus.core.gm.infra.persistence.mysql.department;

import cloud.xcan.angus.core.gm.domain.department.Department;
import cloud.xcan.angus.core.gm.domain.department.DepartmentRepo;
import org.springframework.stereotype.Repository;

/**
 * MySQL implementation of DepartmentRepo
 */
@Repository
public interface DepartmentRepoMysql extends DepartmentRepo {
  // Spring Data JPA will implement methods automatically
}
