package cloud.xcan.angus.core.gm.application.query.group.impl;

import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.group.GroupRepo;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.api.commonlink.user.group.GroupUserNum;
import cloud.xcan.angus.api.commonlink.user.group.GroupUserRepo;
import cloud.xcan.angus.api.manager.SettingTenantQuotaManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.gm.application.converter.UserConverter;
import cloud.xcan.angus.core.gm.application.query.group.GroupUserQuery;
import cloud.xcan.angus.core.gm.domain.group.user.GroupUserListRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * <p>
 * Implementation of group user query operations.
 * </p>
 * <p>
 * Manages group-user relationship queries, validation, and quota management. Provides comprehensive
 * group-user querying with association support.
 * </p>
 * <p>
 * Supports user-group queries, group-user queries, association management, and quota validation for
 * comprehensive group-user administration.
 * </p>
 */
@org.springframework.stereotype.Service
public class GroupUserQueryImpl implements GroupUserQuery {

  @Resource
  private UserRepo userRepo;
  @Resource
  private GroupRepo groupRepo;
  @Resource
  private GroupUserRepo groupUserRepo;
  @Resource
  private GroupUserListRepo groupUserListRepo;
  @Resource
  private SettingTenantQuotaManager settingTenantQuotaManager;

  /**
   * <p>
   * Retrieves user-group relationships with pagination.
   * </p>
   * <p>
   * Queries group associations for specific users with validation. Requires userId parameter for
   * proper filtering.
   * </p>
   */
  @Override
  public Page<GroupUser> findUserGroup(GenericSpecification<GroupUser> spec,
      Pageable pageable) {
    return new BizTemplate<Page<GroupUser>>(true, true) {
      @Override
      protected void checkParams() {
        String userId = findFirstValue(spec.getCriteria(), "userId");
        ProtocolAssert.assertNotEmpty(userId, "userId is required");
      }

      @Override
      protected Page<GroupUser> process() {
        return groupUserListRepo.find(spec.getCriteria(),
            pageable, GroupUser.class, UserConverter::objectArrToGroupUser, null);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves group-user relationships with pagination.
   * </p>
   * <p>
   * Queries user associations for specific groups with validation. Requires groupId parameter for
   * proper filtering.
   * </p>
   */
  @Override
  public Page<GroupUser> findGroupUser(GenericSpecification<GroupUser> spec,
      Pageable pageable) {
    return new BizTemplate<Page<GroupUser>>(true, true) {
      @Override
      protected void checkParams() {
        String groupId = findFirstValue(spec.getCriteria(), "groupId");
        ProtocolAssert.assertNotEmpty(groupId, "groupId is required");
      }

      @Override
      protected Page<GroupUser> process() {
        return groupUserListRepo.find(spec.getCriteria(),
            pageable, GroupUser.class, UserConverter::objectArrToGroupUser, null);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves all group associations for specific user.
   * </p>
   * <p>
   * Returns all groups associated with the specified user. Loads group information for complete
   * association data.
   * </p>
   */
  @Override
  public List<GroupUser> findAllByUserId(Long userId) {
    List<GroupUser> groupUsers = groupUserRepo.findAllByUserId(userId);
    if (isNotEmpty(groupUsers)) {
      List<Group> groups = groupRepo.findAllById(groupUsers.stream().map(GroupUser::getGroupId)
          .collect(Collectors.toList()));
      if (isNotEmpty(groups)) {
        Map<Long, Group> groupMap = groups.stream().collect(Collectors.toMap(Group::getId, x -> x));
        for (GroupUser groupUser : groupUsers) {
          groupUser.setGroup(groupMap.get(groupUser.getGroupId()));
        }
      }
    }
    return groupUsers;
  }

  /**
   * <p>
   * Retrieves user count statistics for groups.
   * </p>
   * <p>
   * Returns user count information grouped by group ID.
   * </p>
   */
  @Override
  public List<GroupUserNum> userCount(Set<Long> groupIds) {
    return groupUserRepo.selectGroupUserNumsGroupByGroupId(groupIds);
  }

  /**
   * <p>
   * Validates user-group replace quota for tenant.
   * </p>
   * <p>
   * Checks if replacing user groups would exceed tenant quota limits. Throws appropriate exception
   * if quota would be exceeded.
   * </p>
   */
  @Override
  public void checkUserGroupReplaceQuota(Long optTenantId, long incr, Long userId) {
    if (incr > 0) {
      // long num = groupUserRepo.countByTenantIdAndUserId(tenantId, userId); <- Replace user groups
      settingTenantQuotaManager
          .checkTenantQuota(QuotaResource.UserGroup, Collections.singleton(userId), /*num +*/ incr);
    }
  }

  /**
   * <p>
   * Validates group-user append quota for tenant.
   * </p>
   * <p>
   * Checks if adding users to group would exceed tenant quota limits. Throws appropriate exception
   * if quota would be exceeded.
   * </p>
   */
  @Override
  public void checkGroupUserAppendQuota(Long optTenantId, long incr, Long groupId) {
    if (incr > 0) {
      long num = groupUserRepo.countByTenantIdAndGroupId(optTenantId, groupId);
      settingTenantQuotaManager
          .checkTenantQuota(QuotaResource.GroupUser, Collections.singleton(groupId), num + incr);
    }
  }

  /**
   * <p>
   * Validates user-group append quota for tenant.
   * </p>
   * <p>
   * Checks if adding groups to user would exceed tenant quota limits. Throws appropriate exception
   * if quota would be exceeded.
   * </p>
   */
  @Override
  public void checkUserGroupAppendQuota(Long optTenantId, long incr, Long userId) {
    if (incr > 0) {
      long num = groupUserRepo.countByTenantIdAndUserId(optTenantId, userId);
      settingTenantQuotaManager
          .checkTenantQuota(QuotaResource.UserGroup, Collections.singleton(userId), num + incr);
    }
  }

  /**
   * <p>
   * Sets group associations for group-user page.
   * </p>
   * <p>
   * Loads group information and associates with group-user records.
   * </p>
   */
  private void setAssociationGroup(Page<GroupUser> userGroupPage) {
    Map<Long, Group> groupMap = groupRepo.findAllById(userGroupPage.getContent().stream()
            .map(GroupUser::getGroupId).collect(Collectors.toList()))
        .stream().collect(Collectors.toMap(Group::getId, x -> x));
    for (GroupUser groupUser : userGroupPage.getContent()) {
      groupUser.setGroup(groupMap.get(groupUser.getGroupId()));
    }
  }

  /**
   * <p>
   * Sets user associations for group-user page.
   * </p>
   * <p>
   * Loads user information and associates with group-user records.
   * </p>
   */
  private void setAssociationUser(Page<GroupUser> userGroupPage) {
    Map<Long, User> userMap = userRepo.findAllById(userGroupPage.getContent().stream()
            .map(GroupUser::getUserId).collect(Collectors.toList()))
        .stream().collect(Collectors.toMap(User::getId, x -> x));
    for (GroupUser groupUser : userGroupPage.getContent()) {
      groupUser.setUser(userMap.get(groupUser.getUserId()));
    }
  }
}
