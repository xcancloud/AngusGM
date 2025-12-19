package cloud.xcan.angus.core.gm.infra.persistence.postgres.department;

import cloud.xcan.angus.core.gm.domain.department.Department;
import cloud.xcan.angus.core.gm.domain.department.DepartmentRepo;
import org.springframework.stereotype.Repository;

/**
 * PostgreSQL implementation of DepartmentRepo
 */
@Repository
public interface DepartmentRepoPostgres extends DepartmentRepo {
  // Spring Data JPA will implement methods automatically
}
