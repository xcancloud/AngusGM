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
import cloud.xcan.angus.core.biz.Biz;
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


@Biz
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
        // Check the tag existed
        tagDb = orgTagQuery.checkAndFind(tagId);
        // Check the tag targets existed
        newTagTargets = new HashSet<>(tagTargets);
        userDb = orgTagTargetQuery.checkUserAndDeduplication(newTagTargets, tagTargets, tagId);
        deptDb = orgTagTargetQuery.checkDeptAndDeduplication(newTagTargets, tagTargets, tagId);
        groupDb = orgTagTargetQuery.checkGroupAndDeduplication(newTagTargets, tagTargets, tagId);
        // Check the target tags quota
        orgTagTargetQuery.checkTargetAppendTagQuota(getOptTenantId(), tagTargets);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        if (isNotEmpty(newTagTargets)) {
          List<IdKey<Long, Object>> idKeys = batchInsert(newTagTargets);
          operationLogCmd.addAll(USER, userDb, TARGET_TAG_UPDATED, tagDb.getName());
          operationLogCmd.addAll(DEPT, deptDb, TARGET_TAG_UPDATED, tagDb.getName());
          operationLogCmd.addAll(GROUP, groupDb, TARGET_TAG_UPDATED, tagDb.getName());
          return idKeys;
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void tagTargetDelete(Long tagId, HashSet<Long> targetIds) {
    new BizTemplate<Void>() {
      OrgTag tagDb;

      @Override
      protected void checkParams() {
        // Check the tag existed
        tagDb = orgTagQuery.checkAndFind(tagId);
      }

      @Override
      protected Void process() {
        orgTagTargetRepo.deleteByTagIdAndTargetIdIn(tagId, targetIds);

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
        Set<OrgTagTarget> tagTargetDb = orgTagTargetRepo
            .findByTagIdInAndTargetTypeAndTargetId(tagIds, OrgTargetType.USER, userId);
        if (isNotEmpty(tagTargetDb)) {
          tagIds.removeAll(tagTargetDb.stream().map(OrgTagTarget::getTagId).toList());
        }

        if (isNotEmpty(tagIds)) {
          orgTagTargetQuery.checkTargetAppendTagQuota(getOptTenantId(), tagIds.size(), userId);

          batchInsert(tagIds.stream()
              .map(x -> new OrgTagTarget().setTagId(x)
                  .setTargetType(OrgTargetType.USER).setTargetId(userId))
              .collect(Collectors.toList()));

          operationLogCmd.add(USER, userDb, TARGET_TAG_UPDATED,
              tagsDb.stream().map(OrgTag::getName).toList().toArray());
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void userTagReplace(Long userId, LinkedHashSet<Long> tagIds) {
    new BizTemplate<Void>() {
      User userDb;

      @Override
      protected void checkParams() {
        userDb = userQuery.checkAndFind(userId);
        if (isNotEmpty(tagIds)) {
          settingTenantQuotaManager.checkTenantQuota(QuotaResource.OrgTargetTag,
              singleton(userId), (long) tagIds.size());
        }
      }

      @Override
      protected Void process() {
        // Clear empty
        deleteAllByTarget(OrgTargetType.USER, singleton(userId));
        // Save new association
        if (isNotEmpty(tagIds)) {
          add(tagIds.stream().map(tagId -> UserTagConverter.toUserTagTarget(tagId, userDb))
              .collect(Collectors.toList()));
        }
        return null;
      }
    }.execute();
  }

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

        operationLogCmd.add(APP, userDb, TARGET_TAG_DELETED,
            tagsDb.stream().map(OrgTag::getName).toList().toArray());
        return null;
      }
    }.execute();
  }

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
        Set<OrgTagTarget> tagTargetDb = orgTagTargetRepo
            .findByTagIdInAndTargetTypeAndTargetId(tagIds, OrgTargetType.DEPT, deptId);
        if (isNotEmpty(tagTargetDb)) {
          tagIds.removeAll(tagTargetDb.stream().map(OrgTagTarget::getTagId).toList());
        }

        if (isNotEmpty(tagIds)) {
          orgTagTargetQuery.checkTargetAppendTagQuota(getOptTenantId(), tagIds.size(), deptId);

          batchInsert(tagIds.stream()
              .map(x -> new OrgTagTarget().setTagId(x).setTargetType(OrgTargetType.DEPT)
                  .setTargetId(deptId))
              .collect(Collectors.toList()));

          operationLogCmd.add(DEPT, deptDb, TARGET_TAG_UPDATED,
              tagsDb.stream().map(OrgTag::getName).toList().toArray());
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deptTagReplace(Long deptId, LinkedHashSet<Long> tagIds) {
    new BizTemplate<Void>() {
      Dept deptDb;

      @Override
      protected void checkParams() {
        deptDb = deptQuery.checkAndFind(deptId);
        settingTenantQuotaManager.checkTenantQuota(QuotaResource.OrgTargetTag,
            singleton(deptId), (long) tagIds.size());
      }

      @Override
      protected Void process() {
        // Clear empty
        deleteAllByTarget(OrgTargetType.DEPT, singleton(deptId));
        // Save new association
        if (isNotEmpty(tagIds)) {
          add(tagIds.stream().map(tagId -> toDeptTagTarget(tagId, deptDb))
              .collect(Collectors.toList()));
        }
        return null;
      }
    }.execute();
  }

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

        operationLogCmd.add(DEPT, deptDb, TARGET_TAG_DELETED,
            tagsDb.stream().map(OrgTag::getName).toList().toArray());
        return null;
      }
    }.execute();
  }

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
        Set<OrgTagTarget> tagTargetDb = orgTagTargetRepo
            .findByTagIdInAndTargetTypeAndTargetId(tagIds, OrgTargetType.GROUP, groupId);
        if (isNotEmpty(tagTargetDb)) {
          tagIds.removeAll(tagTargetDb.stream().map(OrgTagTarget::getTagId).toList());
        }

        if (isNotEmpty(tagIds)) {
          orgTagTargetQuery.checkTargetAppendTagQuota(getOptTenantId(), tagIds.size(), groupId);

          batchInsert(tagIds.stream()
              .map(x -> new OrgTagTarget().setTagId(x)
                  .setTargetType(OrgTargetType.GROUP).setTargetId(groupId))
              .collect(Collectors.toList()));

          operationLogCmd.add(GROUP, groupDb, TARGET_TAG_UPDATED,
              tagsDb.stream().map(OrgTag::getName).toList().toArray());
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void groupTagReplace(Long groupId, LinkedHashSet<Long> tagIds) {
    new BizTemplate<Void>() {
      Group groupDb;

      @Override
      protected void checkParams() {
        groupDb = groupQuery.checkValidAndFind(groupId);
        settingTenantQuotaManager.checkTenantQuota(QuotaResource.OrgTargetTag,
            singleton(groupId), (long) tagIds.size());
      }

      @Override
      protected Void process() {
        // Clear empty
        deleteAllByTarget(OrgTargetType.GROUP, singleton(groupId));
        // Save new association
        if (isNotEmpty(tagIds)) {
          add(tagIds.stream().map(tagId -> toGroupTagTarget(tagId, groupDb))
              .collect(Collectors.toList()));
        }
        return null;
      }
    }.execute();
  }

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

        operationLogCmd.add(GROUP, groupDb, TARGET_TAG_DELETED,
            tagsDb.stream().map(OrgTag::getName).toList().toArray());
        return null;
      }
    }.execute();
  }

  @Override
  public void add(List<OrgTagTarget> tagTargets) {
    // Check the tags existed
    orgTagQuery.checkAndFind(tagTargets.stream().map(OrgTagTarget::getTagId)
        .collect(Collectors.toList()));
    // Check the target tags quota
    orgTagTargetQuery.checkTargetTagQuota(getOptTenantId(), tagTargets);
    batchInsert0(tagTargets);
  }

  @Override
  public void deleteAllByTarget(OrgTargetType targetType, Collection<Long> targetIds) {
    orgTagTargetRepo.deleteByTargetTypeAndTargetIdIn(targetType, targetIds);
  }

  @Override
  public void deleteAllByTenantId(Set<Long> tenantIds) {
    orgTagTargetRepo.deleteAllByTenantId(tenantIds);
  }

  @Override
  protected BaseRepository<OrgTagTarget, Long> getRepository() {
    return this.orgTagTargetRepo;
  }
}
