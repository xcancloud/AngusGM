package cloud.xcan.angus.core.gm.domain.api;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ApiSearchRepo extends CustomBaseRepository<Api> {

}
