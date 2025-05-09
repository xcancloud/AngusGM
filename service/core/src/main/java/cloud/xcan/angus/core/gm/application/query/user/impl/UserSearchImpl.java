package cloud.xcan.angus.core.gm.application.query.user.impl;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.SneakyThrow0;
import cloud.xcan.angus.core.gm.application.query.user.UserSearch;
import cloud.xcan.angus.core.gm.domain.user.UserSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Biz
public class UserSearchImpl implements UserSearch {

  @Resource
  private UserSearchRepo userSearchRepo;

  @Override
  public Page<User> search(Set<SearchCriteria> criteria, Pageable pageable, Class<User> clz,
      String... matches) {
    return new BizTemplate<Page<User>>(true, true) {

      @SneakyThrow0
      @Override
      protected Page<User> process() {
        return userSearchRepo.find(criteria, pageable, clz, matches);
      }
    }.execute();
  }
}
