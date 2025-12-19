package cloud.xcan.angus.core.gm.application.cmd.group.impl;

import static cloud.xcan.angus.core.gm.application.converter.GroupConverter.toGroupUser;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.GROUP;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.USER;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ADD_GROUP_USER;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ADD_USER_GROUP;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETE_GROUP_USER;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETE_USER_GROUP;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATE_USER_GROUP;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.api.commonlink.user.group.GroupUserRepo;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.group.GroupUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.application.query.group.GroupUserQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of group user command operations for managing group-user relationships.
 *
 * <p>This class provides comprehensive functionality for group-user management including:</p>
 * <ul>
 *   <li>Adding users to groups and groups to users</li>
 *   <li>Managing group-user associations</li>
 *   <li>Handling group-user quotas and validations</li>
 *   <li>Recording operation logs for audit trails</li>
 * </ul>
 *
 * <p>The implementation ensures proper group-user relationship management
 * with quota controls and audit trail maintenance.</p>
 */
@org.springframework.stereotype.Service
public class GroupUserCmdImpl extends CommCmd<GroupUser, Long> implements GroupUserCmd {

  @Resource
  private GroupUserRepo groupUserRepo;
  @Resource
  private GroupQuery groupQuery;
  @Resource
  private UserQuery userQuery;
  @Resource
  private GroupUserQuery userGroupQuery;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * Adds groups to a user with comprehensive validation.
   *
   * <p>This method performs group assignment including:</p>
   * <ul>
   *   <li>Validating user existence</li>
   *   <li>Checking user group quota limits</li>
   *   <li>Validating group existence</li>
   *   <li>Checking group user quota limits</li>
   *   <li>Creating group-user associations</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   *
   * @param userId   User identifier
   * @param groupIds Set of group identifiers to assign
   * @return List of created association identifiers
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> groupAdd(Long userId, LinkedHashSet<Long> groupIds) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      User userDb;
      List<Group> groupsDb;

      @Override
      protected void checkParams() {
        // Validate user exists
        userDb = userQuery.checkAndFind(userId);
        // Validate user group quota
        userGroupQuery.checkUserGroupAppendQuota(getOptTenantId(), groupIds.size(), userId);

        // Validate groups exist
        groupsDb = groupQuery.checkValidAndFind(groupIds);
        // Validate group user quota
        for (Long groupId : groupIds) {
          userGroupQuery.checkGroupUserAppendQuota(getOptTenantId(), 1, groupId);
        }
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Remove existing associations to prevent duplicates
        groupUserRepo.deleteByGroupIdInAndUserId(groupIds, userId);

        // Create new group-user associations
        List<GroupUser> groupUsers = groupIds.stream()
            .map(x -> new GroupUser().setGroupId(x).setUserId(userId))
            .collect(Collectors.toList());
        List<IdKey<Long, Object>> idKeys = batchInsert(groupUsers);

        // Record operation audit log
        operationLogCmd.add(USER, userDb, ADD_USER_GROUP,
            groupsDb.stream().map(Group::getName).collect(Collectors.joining(",")));
        return idKeys;
      }
    }.execute();
  }

  /**
   * Replaces user's group associations with new ones.
   *
   * <p>This method performs group replacement including:</p>
   * <ul>
   *   <li>Validating user and group existence</li>
   *   <li>Checking quota limits for replacement</li>
   *   <li>Clearing existing associations</li>
   *   <li>Creating new associations</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   *
   * @param userId   User identifier
   * @param groupIds Set of new group identifiers
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void groupReplace(Long userId, LinkedHashSet<Long> groupIds) {
    new BizTemplate<Void>() {
      User userDb;
      List<Group> groupsDb;

      @Override
      protected void checkParams() {
        userDb = userQuery.checkAndFind(userId);
        groupsDb = groupQuery.checkAndFind(groupIds);

        if (isNotEmpty(groupIds)) {
          // Validate user group quota
          userGroupQuery.checkUserGroupReplaceQuota(getOptTenantId(), groupIds.size(), userId);
          // Validate group user quota
          for (Long groupId : groupIds) {
            userGroupQuery.checkGroupUserAppendQuota(getOptTenantId(), 1, groupId);
          }
        }
      }

      @Override
      protected Void process() {
        // Clear existing associations
        deleteByUserId(Collections.singleton(userId));
        // Create new associations
        if (isNotEmpty(groupIds)) {
          List<GroupUser> groupUsers = groupIds.stream()
              .map(groupId -> toGroupUser(groupId, userDb)).collect(Collectors.toList());
          add0(groupUsers);
        }

        // Record operation audit log
        operationLogCmd.add(USER, userDb, UPDATE_USER_GROUP,
            groupsDb.stream().map(Group::getName).collect(Collectors.joining(",")));
        return null;
      }
    }.execute();
  }

  /**
   * Removes groups from a user.
   *
   * <p>This method performs group removal including:</p>
   * <ul>
   *   <li>Validating user and group existence</li>
   *   <li>Removing group-user associations</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   *
   * @param userId   User identifier
   * @param groupIds Set of group identifiers to remove
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void groupDelete(Long userId, HashSet<Long> groupIds) {
    new BizTemplate<Void>() {
      User userDb;
      List<Group> groupsDb;

      @Override
      protected void checkParams() {
        userDb = userQuery.checkAndFind(userId);
        groupsDb = groupQuery.checkAndFind(groupIds);
      }

      @Override
      protected Void process() {
        // Remove group-user associations
        groupUserRepo.deleteByGroupIdInAndUserId(groupIds, userId);

        // Record operation audit log
        operationLogCmd.add(USER, userDb, DELETE_USER_GROUP,
            groupsDb.stream().map(Group::getName).collect(Collectors.joining(",")));
        return null;
      }
    }.execute();
  }

  /**
   * Adds users to a group with comprehensive validation.
   *
   * <p>This method performs user assignment including:</p>
   * <ul>
   *   <li>Validating group existence</li>
   *   <li>Checking group user quota limits</li>
   *   <li>Validating user existence</li>
   *   <li>Checking user group quota limits</li>
   *   <li>Creating user-group associations</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   *
   * @param groupId    Group identifier
   * @param groupUsers List of user-group associations to create
   * @return List of created association identifiers
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> userAdd(Long groupId, List<GroupUser> groupUsers) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Group groupDb;
      Set<Long> userIds;
      List<User> usersDb;

      @Override
      protected void checkParams() {
        // Validate group exists
        groupDb = groupQuery.checkValidAndFind(groupId);
        // Validate group user quota
        userGroupQuery.checkGroupUserAppendQuota(getOptTenantId(), groupUsers.size(), groupId);

        // Validate users exist
        userIds = groupUsers.stream().map(GroupUser::getUserId).collect(Collectors.toSet());
        usersDb = userQuery.checkAndFind(userIds);
        // Validate user group quota
        for (Long userId : userIds) {
          userGroupQuery.checkUserGroupAppendQuota(getOptTenantId(), 1, userId);
        }
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Remove existing associations to prevent duplicates
        groupUserRepo.deleteByGroupIdAndUserIdIn(groupId, userIds);

        // Create new user-group associations
        List<IdKey<Long, Object>> idKeys = batchInsert(new HashSet<>(groupUsers));

        // Record operation audit log
        operationLogCmd.add(GROUP, groupDb, ADD_GROUP_USER,
            usersDb.stream().map(User::getName).collect(Collectors.joining(",")));
        return idKeys;
      }
    }.execute();
  }

  /**
   * Removes users from a group.
   *
   * <p>This method performs user removal including:</p>
   * <ul>
   *   <li>Validating group and user existence</li>
   *   <li>Removing user-group associations</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   *
   * @param groupId Group identifier
   * @param userIds Set of user identifiers to remove
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void userDelete(Long groupId, Set<Long> userIds) {
    new BizTemplate<Void>() {
      Group groupDb;
      List<User> usersDb;

      @Override
      protected void checkParams() {
        groupDb = groupQuery.checkAndFind(groupId);
        usersDb = userQuery.checkAndFind(userIds);
      }

      @Override
      protected Void process() {
        // Remove user-group associations
        groupUserRepo.deleteByGroupIdAndUserId(groupId, userIds);

        // Record operation audit log
        operationLogCmd.add(GROUP, groupDb, DELETE_GROUP_USER,
            usersDb.stream().map(User::getName).collect(Collectors.joining(",")));
        return null;
      }
    }.execute();
  }

  /**
   * Adds group-user associations with validation.
   *
   * <p>This method ensures groups exist and creates associations.</p>
   *
   * @param groupUsers List of group-user associations to create
   * @return List of created association identifiers
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<GroupUser> groupUsers) {
    if (isEmpty(groupUsers)) {
      return null;
    }
    // Validate groups exist
    groupQuery.checkValidAndFind(groupUsers.stream().map(GroupUser::getGroupId)
        .collect(Collectors.toList()));
    return batchInsert(groupUsers);
  }

  /**
   * Adds group-user associations without validation.
   *
   * @param groupUsers List of group-user associations to create
   * @return List of created association identifiers
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add0(List<GroupUser> groupUsers) {
    if (isEmpty(groupUsers)) {
      return null;
    }
    return batchInsert(groupUsers);
  }

  /**
   * Deletes LDAP garbage relationships by directory.
   *
   * @param directoryId Directory identifier
   * @param groupUsers  List of group-user associations to clean
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete0(Long directoryId, List<GroupUser> groupUsers) {
    // Clean LDAP garbage relationships
    groupUserRepo.deleteByDirectoryIdAndGroupIdInAndUserIdIn(directoryId,
        groupUsers.stream().map(GroupUser::getGroupId).collect(Collectors.toSet()),
        groupUsers.stream().map(GroupUser::getUserId).collect(Collectors.toSet()));
  }

  /**
   * Deletes all group-user associations by group identifiers.
   *
   * @param groupIds Set of group identifiers
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deleteAllByGroupId(Set<Long> groupIds) {
    groupUserRepo.deleteAllByGroupIdIn(groupIds);
  }

  /**
   * Deletes all group-user associations by user identifiers.
   *
   * @param userIds Set of user identifiers
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deleteByUserId(Set<Long> userIds) {
    groupUserRepo.deleteAllByUserIdIn(userIds);
  }

  /**
   * Deletes all group-user associations by tenant identifiers.
   *
   * @param tenantIds Set of tenant identifiers
   */
  @Override
  public void deleteByTenantId(Set<Long> tenantIds) {
    groupUserRepo.deleteByTenantId(tenantIds);
  }

  @Override
  protected GroupUserRepo getRepository() {
    return this.groupUserRepo;
  }
}
