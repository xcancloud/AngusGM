package cloud.xcan.angus.core.gm.domain.tag;

import cloud.xcan.angus.api.commonlink.tag.OrgTag;
import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OrgTagSearchRepo extends CustomBaseRepository<OrgTag> {

}
