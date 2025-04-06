package cloud.xcan.angus.core.gm.domain.tag;

import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OrgTagTargetListRepo extends CustomBaseRepository<OrgTagTarget> {

  StringBuilder getSqlTemplate0(SearchMode mode, Set<SearchCriteria> criteria,
      Class<OrgTagTarget> mainClz, String tableName, String... matches);

  String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params);

}
