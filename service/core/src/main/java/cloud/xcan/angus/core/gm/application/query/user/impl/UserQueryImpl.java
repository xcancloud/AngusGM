package cloud.xcan.angus.core.gm.application.query.user.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.user.User;
import cloud.xcan.angus.core.gm.domain.user.UserRepo;
import cloud.xcan.angus.core.gm.domain.user.UserSearchRepo;
import cloud.xcan.angus.core.gm.domain.user.enums.EnableStatus;
import cloud.xcan.angus.core.gm.domain.user.enums.UserStatus;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Implementation of user query service
 */
@Biz
public class UserQueryImpl implements UserQuery {

  @Resource
  private UserRepo userRepo;

  @Resource
  private UserSearchRepo userSearchRepo;

  @Override
  public User findAndCheck(Long id) {
    return new BizTemplate<User>() {
      @Override
      protected User process() {
        return userRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("用户未找到", new Object[]{}));
      }
    }.execute();
  }

  @Override
  public Page<User> find(GenericSpecification<User> spec, PageRequest pageable,
                         boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<User>>() {
      @Override
      protected Page<User> process() {
        Page<User> page = fullTextSearch
            ? userSearchRepo.find(spec.getCriteria(), pageable, User.class, match)
            : userRepo.findAll(spec, pageable);
        
        // Set associated data for users if needed
        if (page.hasContent()) {
          // TODO: Set role names, department names, online status, etc.
        }
        
        return page;
      }
    }.execute();
  }

  @Override
  public boolean existsByUsername(String username) {
    return userRepo.existsByUsername(username);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userRepo.existsByEmail(email);
  }

  @Override
  public long count() {
    return userRepo.count();
  }

  @Override
  public long countByStatus(UserStatus status) {
    return userRepo.countByStatus(status);
  }

  @Override
  public long countByEnableStatus(EnableStatus enableStatus) {
    return userRepo.countByEnableStatus(enableStatus);
  }
}
