package cloud.xcan.angus.core.gm.domain.event;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface EventSearchRepo extends CustomBaseRepository<Event> {

}
