package cloud.xcan.angus.core.gm.application.cmd.group.impl;

import static cloud.xcan.angus.core.gm.application.converter.GroupTagConverter.dtoToGroupTagsDomain;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.GROUP;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DISABLED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ENABLED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATED;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreTenantAuditing;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.group.GroupRepo;
import cloud.xcan.angus.api.commonlink.group.GroupSource;
import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.group.GroupCmd;
import cloud.xcan.angus.core.gm.application.cmd.group.GroupUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyGroupCmd;
import cloud.xcan.angus.core.gm.application.cmd.tag.OrgTagTargetCmd;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class GroupCmdImpl extends CommCmd<Group, Long> implements GroupCmd {

  @Resource
  private GroupRepo groupRepo;

  @Resource
  private GroupUserCmd userGroupCmd;

  @Resource
  private GroupQuery groupQuery;

  @Resource
  private AuthPolicyGroupCmd authPolicyGroupCmd;

  @Resource
  private OrgTagTargetCmd orgTagTargetCmd;

  @Resource
  private OperationLogCmd operationLogCmd;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<Group> groups) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      final Long optTenantId = getOptTenantId();

      @Override
      protected void checkParams() {
        // Check the code is not repeated
        groupQuery.checkAddGroupCode(optTenantId, groups);
        // Check the group quota
        groupQuery.checkQuota(optTenantId, groups.size());
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Save groups
        List<IdKey<Long, Object>> idKeys = batchInsert(groups);

        // Save groups tags
        List<OrgTagTarget> allTags = buildOrgTagTargets(groups);
        if (isNotEmpty(allTags)) {
          orgTagTargetCmd.add(allTags);
        }

        // Save operation log
        operationLogCmd.addAll(GROUP, groups, CREATED);
        return idKeys;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(List<Group> groups) {
    new BizTemplate<Void>() {
      Set<Long> updateDeptIds;

      @Override
      protected void checkParams() {
        // Check the code is not repeated
        groupQuery.checkUpdateDeptCode(getOptTenantId(), groups);
        // Check the groups existed
        updateDeptIds = groups.stream().map(Group::getId).collect(Collectors.toSet());
        groupQuery.checkValidAndFind(updateDeptIds);
      }

      @Override
      protected Void process() {
        // Update groups
        List<Group> groupsDb = batchUpdateOrNotFound(groups);

        // Replace tags
        List<OrgTagTarget> allTags = buildOrgTagTargets(groups);
        if (isNotEmpty(allTags)) {
          orgTagTargetCmd.deleteAllByTarget(OrgTargetType.GROUP, updateDeptIds);
          orgTagTargetCmd.add(allTags);
        }

        // Save operation log
        operationLogCmd.addAll(GROUP, groupsDb, UPDATED);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> replace(List<Group> groups) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      final Long optTenantId = getOptTenantId();
      List<Group> replaceGroups;
      Set<Long> replaceGroupIds;
      List<Group> replaceGroupsDb;

      @Override
      protected void checkParams() {
        replaceGroups = groups.stream().filter(group -> nonNull(group.getId()))
            .collect(Collectors.toList());
        if (isNotEmpty(replaceGroups)) {
          // Check the updated groups existed
          replaceGroupIds = replaceGroups.stream().map(Group::getId).collect(Collectors.toSet());
          replaceGroupsDb = groupQuery.checkValidAndFind(replaceGroupIds);
          // Check the code is not repeated
          groupQuery.checkUpdateDeptCode(optTenantId, replaceGroups);
        }
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        List<IdKey<Long, Object>> idKeys = new ArrayList<>();

        List<Group> addGroups = groups.stream().filter(group -> isNull(group.getId()))
            .peek(x -> x.setEnabled(Boolean.TRUE).setSource(GroupSource.BACKGROUND_ADDED))
            .collect(Collectors.toList());
        if (isNotEmpty(addGroups)) {
          idKeys.addAll(add(addGroups));
        }

        if (isNotEmpty(replaceGroups)) {
          Map<Long, Group> groupDbMap = replaceGroupsDb.stream()
              .collect(Collectors.toMap(Group::getId, x -> x));
          // Do not replace source, enabled and tenant auditing。
          replaceGroupsDb = replaceGroups.stream()
              .map(x -> copyPropertiesIgnoreTenantAuditing(x, groupDbMap.get(x.getId()),
                  "source", "enabled"))
              .collect(Collectors.toList());
          groupRepo.saveAll(replaceGroupsDb);
          idKeys.addAll(replaceGroupsDb.stream()
              .map(x -> IdKey.of(x.getId(), x.getName())).toList());

          // Replace tags
          List<OrgTagTarget> allTags = buildOrgTagTargets(replaceGroups);
          if (isNotEmpty(allTags)) {
            orgTagTargetCmd.deleteAllByTarget(OrgTargetType.GROUP, replaceGroupIds);
            orgTagTargetCmd.add(allTags);
          }

          // Save operation log
          operationLogCmd.addAll(GROUP, replaceGroupsDb, UPDATED);
        }
        return idKeys;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Set<Long> ids) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        List<Group> groups = groupQuery.findByIdIn(ids);
        if (isNotEmpty(groups)) {
          delete0(ids);

          operationLogCmd.addAll(GROUP, groups, DELETED);
        }
        return null;
      }
    }.execute();
  }

  @Override
  public void delete0(Set<Long> ids) {
    if (isEmpty(ids)) {
      return;
    }

    groupRepo.deleteByIdIn(ids);

    // Delete group association
    userGroupCmd.deleteAllByGroupId(ids);
    orgTagTargetCmd.deleteAllByTarget(OrgTargetType.GROUP, ids);

    // Delete group auth in AAS service
    authPolicyGroupCmd.groupPolicyDeleteBatch(new HashSet<>(ids), null);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(List<Group> groups) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        List<Group> groupsDb = batchUpdateOrNotFound(groups);

        // Save operation log
        operationLogCmd.addAll(GROUP, groupsDb.stream()
            .filter(Group::getEnabled).toList(), ENABLED);
        operationLogCmd.addAll(GROUP, groupsDb.stream()
             .filter(x -> !x.getEnabled()).toList(), DISABLED);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void emptyDirectoryGroups(Set<Long> groupIds) {
    groupRepo.updateDirectoryEmptyByGroupIdIn(groupIds);
  }

  @Override
  public void emptyDirectoryGroups(Long directoryId) {
    groupRepo.updateDirectoryEmptyByDirectoryId(directoryId);
  }

  @Override
  public void deleteByDirectory(Long directoryId, boolean deleteSync) {
    if (deleteSync) {
      delete0(groupRepo.findGroupIdsByDirectoryId(directoryId));
    } else {
      emptyDirectoryGroups(directoryId);
    }
  }

  private List<OrgTagTarget> buildOrgTagTargets(List<Group> groups) {
    return groups.stream().filter(x -> isNotEmpty(x.getTagIds()))
        .map(dept -> dtoToGroupTagsDomain(dept.getTagIds(), dept.getId()))
        .flatMap(Collection::stream).collect(Collectors.toList());
  }

  @Override
  protected BaseRepository<Group, Long> getRepository() {
    return this.groupRepo;
  }
}
