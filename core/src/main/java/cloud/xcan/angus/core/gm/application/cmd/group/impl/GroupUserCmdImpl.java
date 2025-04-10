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
import cloud.xcan.angus.core.biz.Biz;
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

@Biz
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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> groupAdd(Long userId, LinkedHashSet<Long> groupIds) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      User userDb;
      List<Group> groupsDb;

      @Override
      protected void checkParams() {
        // Check the users existed
        userDb = userQuery.checkAndFind(userId);
        // Check the user group quota
        userGroupQuery.checkUserGroupAppendQuota(getOptTenantId(), groupIds.size(), userId);

        // Check the group existed
        groupsDb = groupQuery.checkValidAndFind(groupIds);
        // Check the group user quota
        for (Long groupId : groupIds) {
          userGroupQuery.checkGroupUserAppendQuota(getOptTenantId(), 1, groupId);
        }
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Delete repeated in db
        groupUserRepo.deleteByGroupIdInAndUserId(groupIds, userId);

        List<GroupUser> groupUsers = groupIds.stream()
            .map(x -> new GroupUser().setGroupId(x).setUserId(userId))
            .collect(Collectors.toList());
        List<IdKey<Long, Object>> idKeys = batchInsert(groupUsers);

        operationLogCmd.add(USER, userDb, ADD_USER_GROUP,
            groupsDb.stream().map(Group::getName).toList().toArray());
        return idKeys;
      }
    }.execute();
  }

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
          // Check the user group quota
          userGroupQuery.checkUserGroupReplaceQuota(getOptTenantId(), groupIds.size(), userId);
          // Check the group user quota
          for (Long groupId : groupIds) {
            userGroupQuery.checkGroupUserAppendQuota(getOptTenantId(), 1, groupId);
          }
        }
      }

      @Override
      protected Void process() {
        // Clear empty
        deleteByUserId(Collections.singleton(userId));
        // Save new association
        if (isNotEmpty(groupIds)) {
          List<GroupUser> groupUsers = groupIds.stream()
              .map(groupId -> toGroupUser(groupId, userDb)).collect(Collectors.toList());
          add0(groupUsers);
        }

        operationLogCmd.add(USER, userDb, UPDATE_USER_GROUP,
            groupsDb.stream().map(Group::getName).toList().toArray());
        return null;
      }
    }.execute();
  }

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
        groupUserRepo.deleteByGroupIdInAndUserId(groupIds, userId);

        operationLogCmd.add(USER, userDb, DELETE_USER_GROUP,
            groupsDb.stream().map(Group::getName).toList().toArray());
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> userAdd(Long groupId, List<GroupUser> groupUsers) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Group groupDb;
      Set<Long> userIds;
      List<User> usersDb;

      @Override
      protected void checkParams() {
        // Check the groups existed
        groupDb = groupQuery.checkValidAndFind(groupId);
        // Check the group user quota
        userGroupQuery.checkGroupUserAppendQuota(getOptTenantId(), groupUsers.size(), groupId);

        // Check the users existed
        userIds = groupUsers.stream().map(GroupUser::getUserId).collect(Collectors.toSet());
        usersDb = userQuery.checkAndFind(userIds);
        // Check the user group quota
        for (Long userId : userIds) {
          userGroupQuery.checkUserGroupAppendQuota(getOptTenantId(), 1, userId);
        }
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Delete repeated in db
        groupUserRepo.deleteByGroupIdAndUserIdIn(groupId, userIds);

        List<IdKey<Long, Object>> idKeys = batchInsert(new HashSet<>(groupUsers));

        operationLogCmd.add(GROUP, groupDb, ADD_GROUP_USER,
            usersDb.stream().map(User::getName).toList().toArray());
        return idKeys;
      }
    }.execute();
  }

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
        groupUserRepo.deleteByGroupIdAndUserId(groupId, userIds);

        operationLogCmd.add(GROUP, groupDb, DELETE_GROUP_USER,
            usersDb.stream().map(User::getName).toList().toArray());
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<GroupUser> groupUsers) {
    if (isEmpty(groupUsers)) {
      return null;
    }
    // Check the groups existed
    groupQuery.checkValidAndFind(groupUsers.stream().map(GroupUser::getGroupId)
        .collect(Collectors.toList()));
    return batchInsert(groupUsers);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add0(List<GroupUser> groupUsers) {
    if (isEmpty(groupUsers)) {
      return null;
    }
    return batchInsert(groupUsers);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete0(Long directoryId, List<GroupUser> groupUsers) {
    // Clean LDAP garbage relationships
    groupUserRepo.deleteByDirectoryIdAndGroupIdInAndUserIdIn(directoryId,
        groupUsers.stream().map(GroupUser::getGroupId).collect(Collectors.toSet()),
        groupUsers.stream().map(GroupUser::getUserId).collect(Collectors.toSet()));
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deleteAllByGroupId(Set<Long> groupIds) {
    groupUserRepo.deleteAllByGroupIdIn(groupIds);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deleteByUserId(Set<Long> userIds) {
    groupUserRepo.deleteAllByUserIdIn(userIds);
  }

  @Override
  public void deleteByTenantId(Set<Long> tenantIds) {
    groupUserRepo.deleteByTenantId(tenantIds);
  }

  @Override
  protected GroupUserRepo getRepository() {
    return this.groupUserRepo;
  }
}
