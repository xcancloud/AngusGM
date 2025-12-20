package cloud.xcan.angus.core.gm.interfaces.user.facade.internal;

import static cloud.xcan.angus.spec.BizConstant.buildVoPageResult;
import static cloud.xcan.angus.spec.BizConstant.getMatchSearchFields;

import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.user.User;
import cloud.xcan.angus.core.gm.domain.user.UserRepo;
import cloud.xcan.angus.core.gm.domain.user.enums.EnableStatus;
import cloud.xcan.angus.core.gm.domain.user.enums.UserStatus;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserCreateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserFindDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserAssembler;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserDetailVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserListVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Implementation of user facade
 */
@Service
public class UserFacadeImpl implements UserFacade {

  @Resource
  private UserCmd userCmd;

  @Resource
  private UserQuery userQuery;

  @Resource
  private UserRepo userRepo;

  @Override
  public UserDetailVo create(UserCreateDto dto) {
    User user = UserAssembler.toCreateDomain(dto);
    User saved = userCmd.create(user);
    return UserAssembler.toDetailVo(saved);
  }

  @Override
  public UserDetailVo update(Long id, UserUpdateDto dto) {
    User user = UserAssembler.toUpdateDomain(id, dto);
    User saved = userCmd.update(user);
    return UserAssembler.toDetailVo(saved);
  }

  @Override
  public void enable(Long id) {
    userCmd.enable(id);
  }

  @Override
  public void disable(Long id) {
    userCmd.disable(id);
  }

  @Override
  public void delete(Long id) {
    userCmd.delete(id);
  }

  @Override
  public UserDetailVo getDetail(Long id) {
    User user = userQuery.findAndCheck(id);
    return UserAssembler.toDetailVo(user);
  }

  @Override
  public PageResult<UserListVo> list(UserFindDto dto) {
    GenericSpecification<User> spec = UserAssembler.getSpecification(dto);
    Page<User> page = userQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, UserAssembler::toListVo);
  }

  @Override
  public UserStatsVo getStats() {
    UserStatsVo stats = new UserStatsVo();
    
    // Count users by status
    long totalUsers = userRepo.count();
    long activeUsers = userRepo.countByStatus(UserStatus.ACTIVE);
    long disabledUsers = userRepo.countByEnableStatus(EnableStatus.DISABLED);
    long pendingUsers = userRepo.countByStatus(UserStatus.PENDING);
    
    stats.setTotalUsers(totalUsers);
    stats.setActiveUsers(activeUsers);
    stats.setDisabledUsers(disabledUsers);
    stats.setPendingUsers(pendingUsers);
    
    // TODO: Implement online users count and new users this month
    stats.setOnlineUsers(0L);
    stats.setNewUsersThisMonth(0L);
    
    return stats;
  }

  @Override
  public void lock(Long id) {
    userCmd.lock(id);
  }

  @Override
  public void unlock(Long id) {
    userCmd.unlock(id);
  }

  @Override
  public void resetPassword(Long id, String newPassword) {
    userCmd.resetPassword(id, newPassword);
  }
}
