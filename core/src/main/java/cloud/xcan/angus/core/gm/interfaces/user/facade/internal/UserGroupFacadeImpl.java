package cloud.xcan.angus.core.gm.interfaces.user.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserGroupAssembler.getUserGroupSpecification;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.group.GroupUserCmd;
import cloud.xcan.angus.core.gm.application.query.group.GroupUserQuery;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserGroupFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.group.UserGroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserGroupAssembler;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.group.UserGroupVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserGroupFacadeImpl implements UserGroupFacade {

  @Resource
  private GroupUserCmd userGroupCmd;

  @Resource
  private GroupUserQuery userGroupQuery;

  @Override
  public List<IdKey<Long, Object>> groupAdd(Long userId, LinkedHashSet<Long> groupIds) {
    return userGroupCmd.groupAdd(userId, groupIds);
  }

  @Override
  public void groupReplace(Long userId, LinkedHashSet<Long> groupIds) {
    userGroupCmd.groupReplace(userId, groupIds);
  }

  @Override
  public void groupDelete(Long userId, HashSet<Long> groupIds) {
    userGroupCmd.groupDelete(userId, groupIds);
  }

  @NameJoin
  @Override
  public PageResult<UserGroupVo> groupList(Long userId, UserGroupFindDto dto) {
    dto.setUserId(userId);
    Page<GroupUser> page = userGroupQuery.findUserGroup(
        getUserGroupSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, UserGroupAssembler::toUserGroupVo);
  }

}
