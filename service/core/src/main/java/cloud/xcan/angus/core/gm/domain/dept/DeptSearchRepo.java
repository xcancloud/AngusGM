package cloud.xcan.angus.core.gm.domain.dept;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface DeptSearchRepo extends CustomBaseRepository<Dept> {

}
