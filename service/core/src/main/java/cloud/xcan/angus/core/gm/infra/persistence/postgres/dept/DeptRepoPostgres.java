package cloud.xcan.angus.core.gm.infra.persistence.postgres.dept;

import cloud.xcan.angus.api.commonlink.dept.DeptRepo;
import org.springframework.stereotype.Repository;

@Repository("deptRepo")
public interface DeptRepoPostgres extends DeptRepo {

}
