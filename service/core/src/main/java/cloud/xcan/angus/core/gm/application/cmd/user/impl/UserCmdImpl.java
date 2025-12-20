package cloud.xcan.angus.core.gm.application.cmd.user.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.user.User;
import cloud.xcan.angus.core.gm.domain.user.UserRepo;
import cloud.xcan.angus.core.gm.domain.user.enums.EnableStatus;
import cloud.xcan.angus.core.gm.domain.user.enums.UserStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of user command service
 */
@Biz
public class UserCmdImpl extends CommCmd<User, Long> implements UserCmd {

  @Resource
  private UserRepo userRepo;

  @Resource
  private UserQuery userQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public User create(User user) {
    return new BizTemplate<User>() {
      @Override
      protected void checkParams() {
        if (userRepo.existsByUsername(user.getUsername())) {
          throw ResourceExisted.of("用户名「{0}」已存在", new Object[]{user.getUsername()});
        }
        if (user.getEmail() != null && userRepo.existsByEmail(user.getEmail())) {
          throw ResourceExisted.of("邮箱「{0}」已存在", new Object[]{user.getEmail()});
        }
      }

      @Override
      protected User process() {
        if (user.getStatus() == null) {
          user.setStatus(UserStatus.ACTIVE);
        }
        if (user.getEnableStatus() == null) {
          user.setEnableStatus(EnableStatus.ENABLED);
        }
        if (user.getIsLocked() == null) {
          user.setIsLocked(false);
        }
        insert(user);
        return user;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public User update(User user) {
    return new BizTemplate<User>() {
      User userDb;

      @Override
      protected void checkParams() {
        userDb = userQuery.findAndCheck(user.getId());
        
        if (user.getUsername() != null && !user.getUsername().equals(userDb.getUsername())) {
          if (userRepo.existsByUsernameAndIdNot(user.getUsername(), user.getId())) {
            throw ResourceExisted.of("用户名「{0}」已存在", new Object[]{user.getUsername()});
          }
        }
        
        if (user.getEmail() != null && !user.getEmail().equals(userDb.getEmail())) {
          if (userRepo.existsByEmail(user.getEmail())) {
            throw ResourceExisted.of("邮箱「{0}」已存在", new Object[]{user.getEmail()});
          }
        }
      }

      @Override
      protected User process() {
        update(user, userDb);
        return userDb;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        userQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        userRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void enable(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        userQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        User user = new User();
        user.setId(id);
        user.setEnableStatus(EnableStatus.ENABLED);
        userRepo.save(user);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void disable(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        userQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        User user = new User();
        user.setId(id);
        user.setEnableStatus(EnableStatus.DISABLED);
        userRepo.save(user);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void lock(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        userQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        User user = new User();
        user.setId(id);
        user.setIsLocked(true);
        userRepo.save(user);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void unlock(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        userQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        User user = new User();
        user.setId(id);
        user.setIsLocked(false);
        userRepo.save(user);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void resetPassword(Long id, String newPassword) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        userQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        User user = new User();
        user.setId(id);
        user.setPassword(newPassword); // Should be encrypted
        userRepo.save(user);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<User, Long> getRepository() {
    return userRepo;
  }
}
