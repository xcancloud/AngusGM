package cloud.xcan.angus.core.gm.domain.group;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GroupSearchRepo extends CustomBaseRepository<Group> {

}
