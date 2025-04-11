package cloud.xcan.angus.core.gm.domain.to;

import cloud.xcan.angus.api.commonlink.to.TOUser;
import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TOUserSearchRepo extends CustomBaseRepository<TOUser> {

}
