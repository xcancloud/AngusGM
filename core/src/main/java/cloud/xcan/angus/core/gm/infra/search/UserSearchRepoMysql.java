package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.gm.domain.user.UserListRepo;
import cloud.xcan.angus.core.gm.domain.user.UserSearchRepo;
import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class UserSearchRepoMysql extends AbstractSearchRepository<User> implements
    UserSearchRepo {

  @Resource
  private UserListRepo userListRepo;

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<User> mainClz,
      Object[] params, String... matches) {
    return userListRepo.getSqlTemplate0(getSearchMode(), criteria, mainClz, "user0", matches);
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return userListRepo.getReturnFieldsCondition(criteria, params);
  }

  @Override
  public SearchMode getSearchMode() {
    return SearchMode.MATCH;
  }

}
