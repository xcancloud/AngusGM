package cloud.xcan.angus.core.gm.infra.persistence.postgres.tag;

import cloud.xcan.angus.api.commonlink.tag.OrgTagRepo;
import org.springframework.stereotype.Repository;

@Repository("orgTagRepo")
public interface OrgTagRepoPostgres extends OrgTagRepo {

}
