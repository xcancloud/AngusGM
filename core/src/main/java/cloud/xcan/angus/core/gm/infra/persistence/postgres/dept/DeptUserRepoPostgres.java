package cloud.xcan.angus.core.gm.infra.persistence.postgres.dept;

import cloud.xcan.angus.api.commonlink.user.dept.DeptUserRepo;
import org.springframework.stereotype.Repository;


@Repository("deptUserRepo")
public interface DeptUserRepoPostgres extends DeptUserRepo {

}
