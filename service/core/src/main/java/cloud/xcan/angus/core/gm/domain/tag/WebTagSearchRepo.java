package cloud.xcan.angus.core.gm.domain.tag;

import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface WebTagSearchRepo extends CustomBaseRepository<WebTag> {

}
