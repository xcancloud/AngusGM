package cloud.xcan.angus.core.gm.domain.app;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AppSearchRepo extends CustomBaseRepository<App> {

}
