package cloud.xcan.angus.core.gm.application.cmd.group.impl;

import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.api.commonlink.user.group.GroupUserRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.group.GroupCmd;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.group.Group;
import cloud.xcan.angus.core.gm.domain.group.GroupRepo;
import cloud.xcan.angus.core.gm.domain.group.enums.GroupStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of group command service
 */
@Biz
public class GroupCmdImpl extends CommCmd<Group, Long> implements GroupCmd {

  @Resource
  private GroupRepo groupRepo;

  @Resource
  private GroupQuery groupQuery;

  @Resource
  private GroupUserRepo groupUserRepo;

  @Resource
  private UserQuery userQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Group create(Group group) {
    return new BizTemplate<Group>() {
      @Override
      protected void checkParams() {
        if (groupRepo.existsByCode(group.getCode())) {
          throw ResourceExisted.of("Group code [{0}] already exists", new Object[]{group.getCode()});
        }
      }

      @Override
      protected Group process() {
        if (group.getStatus() == null) {
          group.setStatus(GroupStatus.ENABLED);
        }
        insert(group);
        return group;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Group update(Group group) {
    return new BizTemplate<Group>() {
      Group groupDb;

      @Override
      protected void checkParams() {
        groupDb = groupQuery.findAndCheck(group.getId());
        
        if (group.getCode() != null && !group.getCode().equals(groupDb.getCode())) {
          if (groupRepo.existsByCodeAndIdNot(group.getCode(), group.getId())) {
            throw ResourceExisted.of("Group code [{0}] already exists", new Object[]{group.getCode()});
          }
        }
      }

      @Override
      protected Group process() {
        update(group, groupDb);
        return groupDb;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateStatus(Long id, GroupStatus status) {
    new BizTemplate<Void>() {
      Group groupDb;

      @Override
      protected void checkParams() {
        groupDb = groupQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        groupDb.setStatus(status);
        groupRepo.save(groupDb);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        groupQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        groupRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void addMembers(Long groupId, List<Long> userIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        groupQuery.findAndCheck(groupId);
        
        // Validate all users exist
        if (userIds != null && !userIds.isEmpty()) {
          for (Long userId : userIds) {
            userQuery.findAndCheck(userId);
          }
        }
      }

      @Override
      protected Void process() {
        if (userIds == null || userIds.isEmpty()) {
          return null;
        }
        
        // Get existing group-user relations to avoid duplicates
        List<GroupUser> existingRelations = groupUserRepo.findAllByGroupId(groupId);
        Set<Long> existingUserIds = existingRelations.stream()
            .map(GroupUser::getUserId)
            .collect(Collectors.toSet());
        
        // Filter out users that are already members
        List<Long> newUserIds = userIds.stream()
            .filter(userId -> !existingUserIds.contains(userId))
            .collect(Collectors.toList());
        
        // Create new group-user relations
        if (!newUserIds.isEmpty()) {
          List<GroupUser> newRelations = new ArrayList<>();
          for (Long userId : newUserIds) {
            GroupUser groupUser = new GroupUser();
            groupUser.setGroupId(groupId);
            groupUser.setUserId(userId);
            newRelations.add(groupUser);
          }
          groupUserRepo.saveAll(newRelations);
        }
        
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void removeMember(Long groupId, Long userId) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        groupQuery.findAndCheck(groupId);
      }

      @Override
      protected Void process() {
        // Delete group-user relation using repository method
        groupUserRepo.deleteByGroupIdAndUserId(groupId, List.of(userId));
        
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void removeMembers(Long groupId, List<Long> userIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        groupQuery.findAndCheck(groupId);
      }

      @Override
      protected Void process() {
        if (userIds == null || userIds.isEmpty()) {
          return null;
        }
        
        // Delete group-user relations using repository method
        groupUserRepo.deleteByGroupIdAndUserId(groupId, userIds);
        
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateOwner(Long groupId, Long ownerId) {
    new BizTemplate<Void>() {
      Group groupDb;

      @Override
      protected void checkParams() {
        groupDb = groupQuery.findAndCheck(groupId);
        
        // Validate ownerId exists in user table
        if (ownerId != null) {
          userQuery.findAndCheck(ownerId);
        }
      }

      @Override
      protected Void process() {
        groupDb.setOwnerId(ownerId);
        groupRepo.save(groupDb);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Group, Long> getRepository() {
    return groupRepo;
  }
}
