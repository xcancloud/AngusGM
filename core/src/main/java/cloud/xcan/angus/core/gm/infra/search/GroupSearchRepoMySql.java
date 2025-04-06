package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.core.gm.domain.group.GroupListRepo;
import cloud.xcan.angus.core.gm.domain.group.GroupSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class GroupSearchRepoMySql extends SimpleSearchRepository<Group> implements GroupSearchRepo {

  @Resource
  private GroupListRepo groupListRepo;

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<Group> mainClz,
      Object[] params, String... matches) {
    return groupListRepo.getSqlTemplate0(getSearchMode(), criteria, mainClz, "group0", matches);
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return groupListRepo.getReturnFieldsCondition(criteria, params);
  }

  @Override
  public SearchMode getSearchMode() {
    return super.getSearchMode();
  }
}
