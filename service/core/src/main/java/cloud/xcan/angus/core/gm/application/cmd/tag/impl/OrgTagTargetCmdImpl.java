package cloud.xcan.angus.core.gm.application.cmd.tag.impl;

import static cloud.xcan.angus.core.gm.application.converter.DeptTagConverter.toDeptTagTarget;
import static cloud.xcan.angus.core.gm.application.converter.GroupTagConverter.toGroupTagTarget;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.APP;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.DEPT;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.GROUP;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.USER;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.TARGET_TAG_DELETED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.TARGET_TAG_UPDATED;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Collections.singleton;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.tag.OrgTag;
import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tag.OrgTagTargetRepo;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.manager.SettingTenantQuotaManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.tag.OrgTagTargetCmd;
import cloud.xcan.angus.core.gm.application.converter.UserTagConverter;
import cloud.xcan.angus.core.gm.application.query.dept.DeptQuery;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.application.query.tag.OrgTagQuery;
import cloud.xcan.angus.core.gm.application.query.tag.OrgTagTargetQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of organization tag target command operations.
 * </p>
 * <p>
 * Manages tag-target associations for users, departments, and groups. Provides comprehensive tag
 * assignment, replacement, and deletion functionality.
 * </p>
 * <p>
 * Supports quota validation, deduplication, and audit logging for all tag operations. Handles both
 * individual and batch tag-target operations.
 * </p>
 */
@org.springframework.stereotype.Service
@Slf4j
public class OrgTagTargetCmdImpl extends CommCmd<OrgTagTarget, Long> implements OrgTagTargetCmd {

  @Resource
  private OrgTagTargetRepo orgTagTargetRepo;
  @Resource
  private OrgTagQuery orgTagQuery;
  @Resource
  private OrgTagTargetQuery orgTagTargetQuery;
  @Resource
  private UserQuery userQuery;
  @Resource
  private DeptQuery deptQuery;
  @Resource
  private GroupQuery groupQuery;
  @Resource
  private SettingTenantQuotaManager settingTenantQuotaManager;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * <p>
   * Adds tag targets to a specific tag.
   * </p>
   * <p>
   * Validates tag existence and target deduplication for users, departments, and groups. Checks
   * quota limits and logs operations for audit purposes.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> tagTargetAdd(Long tagId, List<OrgTagTarget> tagTargets) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      OrgTag tagDb;
      List<User> userDb;
      List<Dept> deptDb;
      List<Group> groupDb;
      Set<OrgTagTarget> newTagTargets;

      @Override
      protected void checkParams() {
        // Verify tag exists
        tagDb = orgTagQuery.checkAndFind(tagId);
        // Verify tag targets and perform deduplication
        newTagTargets = new HashSet<>(tagTargets);
        userDb = orgTagTargetQuery.checkUserAndDeduplication(newTagTargets, tagTargets, tagId);
        deptDb = orgTagTargetQuery.checkDeptAndDeduplication(newTagTargets, tagTargets, tagId);
        groupDb = orgTagTargetQuery.checkGroupAndDeduplication(newTagTargets, tagTargets, tagId);
        // Verify target tags quota
        orgTagTargetQuery.checkTargetAppendTagQuota(getOptTenantId(), tagTargets);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        if (isNotEmpty(newTagTargets)) {
          List<IdKey<Long, Object>> idKeys = batchInsert(newTagTargets);
          // Log operations for audit
          operationLogCmd.addAll(USER, userDb, TARGET_TAG_UPDATED, tagDb.getName());
          operationLogCmd.addAll(DEPT, deptDb, TARGET_TAG_UPDATED, tagDb.getName());
          operationLogCmd.addAll(GROUP, groupDb, TARGET_TAG_UPDATED, tagDb.getName());
          return idKeys;
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Deletes tag targets from a specific tag.
   * </p>
   * <p>
   * Removes tag-target associations and logs operations for audit purposes.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void tagTargetDelete(Long tagId, HashSet<Long> targetIds) {
    new BizTemplate<Void>() {
      OrgTag tagDb;

      @Override
      protected void checkParams() {
        // Verify tag exists
        tagDb = orgTagQuery.checkAndFind(tagId);
      }

      @Override
      protected Void process() {
        orgTagTargetRepo.deleteByTagIdAndTargetIdIn(tagId, targetIds);

        // Log operations for audit
        List<User> userDb = userQuery.findByIdIn(targetIds);
        operationLogCmd.addAll(USER, userDb, TARGET_TAG_DELETED, tagDb.getName());
        List<Dept> deptDb = deptQuery.findByIdIn(targetIds);
        operationLogCmd.addAll(DEPT, deptDb, TARGET_TAG_DELETED, tagDb.getName());
        List<Group> groupDb = groupQuery.findByIdIn(targetIds);
        operationLogCmd.addAll(GROUP, groupDb, TARGET_TAG_DELETED, tagDb.getName());
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Adds tags to a specific user.
   * </p>
   * <p>
   * Validates user and tag existence, performs deduplication, and checks quota limits. Logs
   * operations for audit purposes.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> userTagAdd(Long userId, LinkedHashSet<Long> tagIds) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      User userDb;
      List<OrgTag> tagsDb;

      @Override
      protected void checkParams() {
        userDb = userQuery.checkAndFind(userId);
        tagsDb = orgTagQuery.checkAndFind(tagIds);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Check existing tag associations and remove duplicates
        Set<OrgTagTarget> tagTargetDb = orgTagTargetRepo
            .findByTagIdInAndTargetTypeAndTargetId(tagIds, OrgTargetType.USER, userId);
        if (isNotEmpty(tagTargetDb)) {
          tagTargetDb.stream().map(OrgTagTarget::getTagId).toList().forEach(tagIds::remove);
        }

        if (isNotEmpty(tagIds)) {
          // Verify quota for new tag associations
          orgTagTargetQuery.checkTargetAppendTagQuota(getOptTenantId(), tagIds.size(), userId);

          batchInsert(tagIds.stream()
              .map(x -> new OrgTagTarget().setTagId(x)
                  .setTargetType(OrgTargetType.USER).setTargetId(userId))
              .collect(Collectors.toList()));

          // Log operation for audit
          operationLogCmd.add(USER, userDb, TARGET_TAG_UPDATED,
              tagsDb.stream().map(OrgTag::getName).collect(Collectors.joining(",")));
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Replaces all tags for a specific user.
   * </p>
   * <p>
   * Clears existing tag associations and assigns new tags with quota validation.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void userTagReplace(Long userId, LinkedHashSet<Long> tagIds) {
    new BizTemplate<Void>() {
      User userDb;

      @Override
      protected void checkParams() {
        userDb = userQuery.checkAndFind(userId);
        if (isNotEmpty(tagIds)) {
          // Verify quota for tag replacement
          settingTenantQuotaManager.checkTenantQuota(QuotaResource.OrgTargetTag,
              singleton(userId), (long) tagIds.size());
        }
      }

      @Override
      protected Void process() {
        // Clear existing tag associations
        deleteAllByTarget(OrgTargetType.USER, singleton(userId));
        // Save new tag associations
        if (isNotEmpty(tagIds)) {
          add(tagIds.stream().map(tagId -> UserTagConverter.toUserTagTarget(tagId, userDb))
              .collect(Collectors.toList()));
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Deletes specific tags from a user.
   * </p>
   * <p>
   * Removes tag associations and logs the operation for audit purposes.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void userTagDelete(Long userId, HashSet<Long> tagIds) {
    new BizTemplate<Void>() {
      User userDb;
      List<OrgTag> tagsDb;

      @Override
      protected void checkParams() {
        userDb = userQuery.checkAndFind(userId);
        tagsDb = orgTagQuery.checkAndFind(tagIds);
      }

      @Override
      protected Void process() {
        orgTagTargetRepo.deleteByTagIdInAndTargetId(tagIds, userId);

        // Log operation for audit
        operationLogCmd.add(APP, userDb, TARGET_TAG_DELETED,
            tagsDb.stream().map(OrgTag::getName).collect(Collectors.joining(",")));
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Adds tags to a specific department.
   * </p>
   * <p>
   * Validates department and tag existence, performs deduplication, and checks quota limits. Logs
   * operations for audit purposes.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> deptTagAdd(Long deptId, LinkedHashSet<Long> tagIds) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Dept deptDb;
      List<OrgTag> tagsDb;

      @Override
      protected void checkParams() {
        deptDb = deptQuery.checkAndFind(deptId);
        tagsDb = orgTagQuery.checkAndFind(tagIds);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Check existing tag associations and remove duplicates
        Set<OrgTagTarget> tagTargetDb = orgTagTargetRepo
            .findByTagIdInAndTargetTypeAndTargetId(tagIds, OrgTargetType.DEPT, deptId);
        if (isNotEmpty(tagTargetDb)) {
          tagTargetDb.stream().map(OrgTagTarget::getTagId).toList().forEach(tagIds::remove);
        }

        if (isNotEmpty(tagIds)) {
          // Verify quota for new tag associations
          orgTagTargetQuery.checkTargetAppendTagQuota(getOptTenantId(), tagIds.size(), deptId);

          batchInsert(tagIds.stream()
              .map(x -> new OrgTagTarget().setTagId(x).setTargetType(OrgTargetType.DEPT)
                  .setTargetId(deptId))
              .collect(Collectors.toList()));

          // Log operation for audit
          operationLogCmd.add(DEPT, deptDb, TARGET_TAG_UPDATED,
              tagsDb.stream().map(OrgTag::getName).collect(Collectors.joining(",")));
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Replaces all tags for a specific department.
   * </p>
   * <p>
   * Clears existing tag associations and assigns new tags with quota validation.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deptTagReplace(Long deptId, LinkedHashSet<Long> tagIds) {
    new BizTemplate<Void>() {
      Dept deptDb;

      @Override
      protected void checkParams() {
        deptDb = deptQuery.checkAndFind(deptId);
        // Verify quota for tag replacement
        settingTenantQuotaManager.checkTenantQuota(QuotaResource.OrgTargetTag,
            singleton(deptId), (long) tagIds.size());
      }

      @Override
      protected Void process() {
        // Clear existing tag associations
        deleteAllByTarget(OrgTargetType.DEPT, singleton(deptId));
        // Save new tag associations
        if (isNotEmpty(tagIds)) {
          add(tagIds.stream().map(tagId -> toDeptTagTarget(tagId, deptDb))
              .collect(Collectors.toList()));
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Deletes specific tags from a department.
   * </p>
   * <p>
   * Removes tag associations and logs the operation for audit purposes.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deptTagDelete(Long deptId, HashSet<Long> tagIds) {
    new BizTemplate<Void>() {
      Dept deptDb;
      List<OrgTag> tagsDb;

      @Override
      protected void checkParams() {
        deptDb = deptQuery.checkAndFind(deptId);
        tagsDb = orgTagQuery.checkAndFind(tagIds);
      }

      @Override
      protected Void process() {
        orgTagTargetRepo.deleteByTagIdInAndTargetId(tagIds, deptId);

        // Log operation for audit
        operationLogCmd.add(DEPT, deptDb, TARGET_TAG_DELETED,
            tagsDb.stream().map(OrgTag::getName).collect(Collectors.joining(",")));
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Adds tags to a specific group.
   * </p>
   * <p>
   * Validates group and tag existence, performs deduplication, and checks quota limits. Logs
   * operations for audit purposes.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> groupTagAdd(Long groupId, LinkedHashSet<Long> tagIds) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Group groupDb;
      List<OrgTag> tagsDb;

      @Override
      protected void checkParams() {
        groupDb = groupQuery.checkValidAndFind(groupId);
        tagsDb = orgTagQuery.checkAndFind(tagIds);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Check existing tag associations and remove duplicates
        Set<OrgTagTarget> tagTargetDb = orgTagTargetRepo
            .findByTagIdInAndTargetTypeAndTargetId(tagIds, OrgTargetType.GROUP, groupId);
        if (isNotEmpty(tagTargetDb)) {
          tagTargetDb.stream().map(OrgTagTarget::getTagId).toList().forEach(tagIds::remove);
        }

        if (isNotEmpty(tagIds)) {
          // Verify quota for new tag associations
          orgTagTargetQuery.checkTargetAppendTagQuota(getOptTenantId(), tagIds.size(), groupId);

          batchInsert(tagIds.stream()
              .map(x -> new OrgTagTarget().setTagId(x)
                  .setTargetType(OrgTargetType.GROUP).setTargetId(groupId))
              .collect(Collectors.toList()));

          // Log operation for audit
          operationLogCmd.add(GROUP, groupDb, TARGET_TAG_UPDATED,
              tagsDb.stream().map(OrgTag::getName).collect(Collectors.joining(",")));
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Replaces all tags for a specific group.
   * </p>
   * <p>
   * Clears existing tag associations and assigns new tags with quota validation.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void groupTagReplace(Long groupId, LinkedHashSet<Long> tagIds) {
    new BizTemplate<Void>() {
      Group groupDb;

      @Override
      protected void checkParams() {
        groupDb = groupQuery.checkValidAndFind(groupId);
        // Verify quota for tag replacement
        settingTenantQuotaManager.checkTenantQuota(QuotaResource.OrgTargetTag,
            singleton(groupId), (long) tagIds.size());
      }

      @Override
      protected Void process() {
        // Clear existing tag associations
        deleteAllByTarget(OrgTargetType.GROUP, singleton(groupId));
        // Save new tag associations
        if (isNotEmpty(tagIds)) {
          add(tagIds.stream().map(tagId -> toGroupTagTarget(tagId, groupDb))
              .collect(Collectors.toList()));
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Deletes specific tags from a group.
   * </p>
   * <p>
   * Removes tag associations and logs the operation for audit purposes.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void groupTagDelete(Long groupId, HashSet<Long> tagIds) {
    new BizTemplate<Void>() {
      Group groupDb;
      List<OrgTag> tagsDb;

      @Override
      protected void checkParams() {
        groupDb = groupQuery.checkValidAndFind(groupId);
        tagsDb = orgTagQuery.checkAndFind(tagIds);
      }

      @Override
      protected Void process() {
        orgTagTargetRepo.deleteByTagIdInAndTargetId(tagIds, groupId);

        // Log operation for audit
        operationLogCmd.add(GROUP, groupDb, TARGET_TAG_DELETED,
            tagsDb.stream().map(OrgTag::getName).collect(Collectors.joining(",")));
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Adds tag targets with validation.
   * </p>
   * <p>
   * Validates tag existence and quota limits before adding tag targets.
   * </p>
   */
  @Override
  public void add(List<OrgTagTarget> tagTargets) {
    // Verify tags exist
    orgTagQuery.checkAndFind(tagTargets.stream().map(OrgTagTarget::getTagId)
        .collect(Collectors.toList()));
    // Verify target tags quota
    orgTagTargetQuery.checkTargetTagQuota(getOptTenantId(), tagTargets);
    batchInsert0(tagTargets);
  }

  /**
   * <p>
   * Deletes all tag targets for specific target types and IDs.
   * </p>
   * <p>
   * Removes all tag associations for the specified target types and IDs.
   * </p>
   */
  @Override
  public void deleteAllByTarget(OrgTargetType targetType, Collection<Long> targetIds) {
    orgTagTargetRepo.deleteByTargetTypeAndTargetIdIn(targetType, targetIds);
  }

  /**
   * <p>
   * Deletes all tag targets for specific tenant IDs.
   * </p>
   * <p>
   * Removes all tag associations for the specified tenant IDs.
   * </p>
   */
  @Override
  public void deleteAllByTenantId(Set<Long> tenantIds) {
    orgTagTargetRepo.deleteAllByTenantId(tenantIds);
  }

  @Override
  protected BaseRepository<OrgTagTarget, Long> getRepository() {
    return this.orgTagTargetRepo;
  }
}
