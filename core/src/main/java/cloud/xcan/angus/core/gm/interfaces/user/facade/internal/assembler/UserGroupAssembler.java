package cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.user.GroupUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.group.UserGroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.group.UserGroupVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserGroupAssembler {

  public static UserGroupVo toUserGroupVo(GroupUser groupUser) {
    return new UserGroupVo().setId(groupUser.getId())
        .setGroupId(groupUser.getGroupId())
        .setGroupName(groupUser.getGroupName())
        .setGroupCode(groupUser.getGroupCode())
        .setGroupEnabled(groupUser.getGroupEnabled())
        .setGroupRemark(groupUser.getGroupRemark())
        .setUserId(groupUser.getUserId())
        .setFullname(groupUser.getFullname())
        .setCreatedBy(groupUser.getCreatedBy())
        .setCreatedDate(groupUser.getCreatedDate())
        .setAvatar(groupUser.getAvatar())
        .setMobile(groupUser.getMobile())
        .setTenantId(groupUser.getTenantId());
  }

  public static List<GroupUser> groupUserAddToDomain(Long groupId, HashSet<Long> userIds) {
    return userIds.stream().map(uid -> new GroupUser()
        .setGroupId(groupId).setUserId(uid)).collect(Collectors.toList());
  }

  public static GenericSpecification<GroupUser> getUserGroupSpecification(UserGroupFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("createdDate")
        .orderByFields("createdDate")
        .matchSearchFields("groupName")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<GroupUser> getGroupUserSpecification(GroupUserFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("createdDate")
        .orderByFields("createdDate")
        .matchSearchFields("fullname")
        .build();
    return new GenericSpecification<>(filters);
  }
}
