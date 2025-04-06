package cloud.xcan.angus.core.gm.domain.app.func;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AppFuncSearchRepo extends CustomBaseRepository<AppFunc> {

}
