package cloud.xcan.angus.core.gm.application.query.group.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.domain.group.Group;
import cloud.xcan.angus.core.gm.domain.group.GroupRepo;
import cloud.xcan.angus.core.gm.domain.group.GroupSearchRepo;
import cloud.xcan.angus.core.gm.domain.group.enums.GroupType;
import cloud.xcan.angus.core.gm.domain.user.User;
import cloud.xcan.angus.core.gm.domain.user.UserRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Implementation of group query service
 */
@Biz
public class GroupQueryImpl implements GroupQuery {

  @Resource
  private GroupRepo groupRepo;

  @Resource
  private GroupSearchRepo groupSearchRepo;

  @Resource(name = "commonGroupUserRepo")
  private GroupUserRepo groupUserRepo;

  @Resource
  private UserRepo userRepo;

  @Override
  public Group findAndCheck(Long id) {
    return new BizTemplate<Group>() {
      @Override
      protected Group process() {
        return groupRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("组未找到", new Object[]{}));
      }
    }.execute();
  }

  @Override
  public Page<Group> find(GenericSpecification<Group> spec, PageRequest pageable,
                          boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Group>>() {
      @Override
      protected Page<Group> process() {
        Page<Group> page = fullTextSearch
            ? groupSearchRepo.find(spec.getCriteria(), pageable, Group.class, match)
            : groupRepo.findAll(spec, pageable);
        
        // Set associated data if needed
        if (page.hasContent()) {
          // TODO: Set owner names, member counts
        }
        
        return page;
      }
    }.execute();
  }

  @Override
  public boolean existsByCode(String code) {
    return groupRepo.existsByCode(code);
  }

  @Override
  public long count() {
    return groupRepo.count();
  }

  @Override
  public long countByType(GroupType type) {
    return groupRepo.countByType(type);
  }

  @Override
  public List<Group> findByUserId(Long userId) {
    return new BizTemplate<List<Group>>() {
      @Override
      protected List<Group> process() {
        // Find group-user relations by userId
        List<cloud.xcan.angus.api.commonlink.user.group.GroupUser> groupUsers = 
            groupUserRepo.findAllByUserId(userId);
        
        if (groupUsers.isEmpty()) {
          return new ArrayList<>();
        }
        
        // Extract group IDs
        Set<Long> groupIds = groupUsers.stream()
            .map(cloud.xcan.angus.api.commonlink.user.group.GroupUser::getGroupId)
            .collect(Collectors.toSet());
        
        // Find groups by IDs
        return groupRepo.findAllById(groupIds);
      }
    }.execute();
  }

  @Override
  public List<Long> findUserIdsByGroupId(Long groupId) {
    return new BizTemplate<List<Long>>() {
      @Override
      protected List<Long> process() {
        List<cloud.xcan.angus.api.commonlink.user.group.GroupUser> groupUsers = 
            groupUserRepo.findAllByGroupId(groupId);
        return groupUsers.stream()
            .map(cloud.xcan.angus.api.commonlink.user.group.GroupUser::getUserId)
            .collect(Collectors.toList());
      }
    }.execute();
  }

  @Override
  public Page<User> findMembers(Long groupId, GenericSpecification<User> spec, PageRequest pageable) {
    return new BizTemplate<Page<User>>() {
      @Override
      protected Page<User> process() {
        // Get user IDs from group-user relation
        List<Long> userIds = findUserIdsByGroupId(groupId);
        
        if (userIds.isEmpty()) {
          return org.springframework.data.domain.Page.empty(pageable);
        }
        
        // Query users with specification and pagination
        return userRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public long countActiveMembers() {
    return new BizTemplate<Long>() {
      @Override
      protected Long process() {
        // Count distinct users in all groups
        // TODO: Use native query to count distinct users from group_user table
        // For now, return count from group-user relations
        return (long) groupUserRepo.findAll().stream()
            .map(cloud.xcan.angus.api.commonlink.user.group.GroupUser::getUserId)
            .distinct()
            .count();
      }
    }.execute();
  }

  @Override
  public long countNewGroupsThisMonth() {
    return new BizTemplate<Long>() {
      @Override
      protected Long process() {
        LocalDateTime startOfMonth = LocalDateTime.now()
            .with(TemporalAdjusters.firstDayOfMonth())
            .withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        // Count groups created this month
        return groupRepo.findAll().stream()
            .filter(group -> group.getCreatedDate() != null 
                && group.getCreatedDate().isAfter(startOfMonth))
            .count();
      }
    }.execute();
  }
}
