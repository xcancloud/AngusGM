package cloud.xcan.angus.core.gm.interfaces.group.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserGroupAssembler.getGroupUserSpecification;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserGroupAssembler.groupUserAddToDomain;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.group.GroupUserCmd;
import cloud.xcan.angus.core.gm.application.query.group.GroupUserQuery;
import cloud.xcan.angus.core.gm.interfaces.group.facade.GroupUserFacade;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.user.GroupUserFindDto;
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
public class GroupUserFacadeImpl implements GroupUserFacade {

  @Resource
  private GroupUserCmd userGroupCmd;

  @Resource
  private GroupUserQuery userGroupQuery;

  @Override
  public List<IdKey<Long, Object>> userAdd(Long groupId, LinkedHashSet<Long> groupIds) {
    return userGroupCmd.userAdd(groupId, groupUserAddToDomain(groupId, groupIds));
  }

  @Override
  public void userDelete(Long id, HashSet<Long> uIds) {
    userGroupCmd.userDelete(id, uIds);
  }

  @NameJoin
  @Override
  public PageResult<UserGroupVo> groupUserList(Long groupId, GroupUserFindDto dto) {
    dto.setGroupId(groupId);
    Page<GroupUser> userGroupPage = userGroupQuery.findGroupUser(getGroupUserSpecification(dto),
        dto.tranPage());
    return buildVoPageResult(userGroupPage, UserGroupAssembler::toUserGroupVo);
  }
}
