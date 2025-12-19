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

/**
 * Implementation of group command operations for managing organizational groups.
 *
 * <p>This class provides comprehensive functionality for group management including:</p>
 * <ul>
 *   <li>Creating and configuring organizational groups</li>
 *   <li>Managing group-user associations and tags</li>
 *   <li>Handling group enabled/disabled status</li>
 *   <li>Managing directory-based group operations</li>
 *   <li>Recording operation logs for audit trails</li>
 * </ul>
 *
 * <p>The implementation ensures proper group management with validation,
 * tag associations, and audit trail maintenance.</p>
 */
@org.springframework.stereotype.Service
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

  /**
   * Creates multiple groups with comprehensive validation.
   *
   * <p>This method performs group creation including:</p>
   * <ul>
   *   <li>Validating group code uniqueness</li>
   *   <li>Checking group quota limits</li>
   *   <li>Creating group configurations</li>
   *   <li>Managing group tag associations</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   *
   * @param groups List of groups to create
   * @return List of created group identifiers
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<Group> groups) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      final Long optTenantId = getOptTenantId();

      @Override
      protected void checkParams() {
        // Validate group code uniqueness
        groupQuery.checkAddGroupCode(optTenantId, groups);
        // Validate group quota
        groupQuery.checkQuota(optTenantId, groups.size());
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Save groups
        List<IdKey<Long, Object>> idKeys = batchInsert(groups);

        // Save group tag associations
        List<OrgTagTarget> allTags = buildOrgTagTargets(groups);
        if (isNotEmpty(allTags)) {
          orgTagTargetCmd.add(allTags);
        }

        // Record operation audit logs
        operationLogCmd.addAll(GROUP, groups, CREATED);
        return idKeys;
      }
    }.execute();
  }

  /**
   * Updates multiple groups with comprehensive validation.
   *
   * <p>This method performs group updates including:</p>
   * <ul>
   *   <li>Validating group code uniqueness</li>
   *   <li>Checking group existence</li>
   *   <li>Updating group configurations</li>
   *   <li>Replacing group tag associations</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   *
   * @param groups List of groups to update
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(List<Group> groups) {
    new BizTemplate<Void>() {
      Set<Long> updateDeptIds;

      @Override
      protected void checkParams() {
        // Validate group code uniqueness
        groupQuery.checkUpdateDeptCode(getOptTenantId(), groups);
        // Validate groups exist
        updateDeptIds = groups.stream().map(Group::getId).collect(Collectors.toSet());
        groupQuery.checkValidAndFind(updateDeptIds);
      }

      @Override
      protected Void process() {
        // Update groups
        List<Group> groupsDb = batchUpdateOrNotFound(groups);

        // Replace tag associations
        List<OrgTagTarget> allTags = buildOrgTagTargets(groups);
        if (isNotEmpty(allTags)) {
          orgTagTargetCmd.deleteAllByTarget(OrgTargetType.GROUP, updateDeptIds);
          orgTagTargetCmd.add(allTags);
        }

        // Record operation audit logs
        operationLogCmd.addAll(GROUP, groupsDb, UPDATED);
        return null;
      }
    }.execute();
  }

  /**
   * Replaces groups with comprehensive validation and creation.
   *
   * <p>This method performs group replacement including:</p>
   * <ul>
   *   <li>Validating existing groups</li>
   *   <li>Creating new groups for null IDs</li>
   *   <li>Updating existing groups</li>
   *   <li>Managing tag associations</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   *
   * @param groups List of groups to replace
   * @return List of created/updated group identifiers
   */
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
        // Filter groups with existing IDs
        replaceGroups = groups.stream().filter(group -> nonNull(group.getId()))
            .collect(Collectors.toList());
        if (isNotEmpty(replaceGroups)) {
          // Validate existing groups
          replaceGroupIds = replaceGroups.stream().map(Group::getId).collect(Collectors.toSet());
          replaceGroupsDb = groupQuery.checkValidAndFind(replaceGroupIds);
          // Validate group code uniqueness
          groupQuery.checkUpdateDeptCode(optTenantId, replaceGroups);
        }
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        List<IdKey<Long, Object>> idKeys = new ArrayList<>();

        // Create new groups for null IDs
        List<Group> addGroups = groups.stream().filter(group -> isNull(group.getId()))
            .peek(x -> x.setEnabled(Boolean.TRUE).setSource(GroupSource.BACKGROUND_ADDED))
            .collect(Collectors.toList());
        if (isNotEmpty(addGroups)) {
          idKeys.addAll(add(addGroups));
        }

        // Update existing groups
        if (isNotEmpty(replaceGroups)) {
          Map<Long, Group> groupDbMap = replaceGroupsDb.stream()
              .collect(Collectors.toMap(Group::getId, x -> x));
          // Preserve source, enabled and tenant auditing properties
          replaceGroupsDb = replaceGroups.stream()
              .map(x -> copyPropertiesIgnoreTenantAuditing(x, groupDbMap.get(x.getId()),
                  "source", "enabled"))
              .collect(Collectors.toList());
          groupRepo.saveAll(replaceGroupsDb);
          idKeys.addAll(replaceGroupsDb.stream()
              .map(x -> IdKey.of(x.getId(), x.getName())).toList());

          // Replace tag associations
          List<OrgTagTarget> allTags = buildOrgTagTargets(replaceGroups);
          if (isNotEmpty(allTags)) {
            orgTagTargetCmd.deleteAllByTarget(OrgTargetType.GROUP, replaceGroupIds);
            orgTagTargetCmd.add(allTags);
          }

          // Record operation audit logs
          operationLogCmd.addAll(GROUP, replaceGroupsDb, UPDATED);
        }
        return idKeys;
      }
    }.execute();
  }

  /**
   * Deletes groups by their identifiers.
   *
   * <p>This method performs group deletion including:</p>
   * <ul>
   *   <li>Retrieving group information for audit logs</li>
   *   <li>Deleting group configurations</li>
   *   <li>Cleaning up related associations</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   *
   * @param ids Set of group identifiers to delete
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Set<Long> ids) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        // Retrieve groups for audit logging
        List<Group> groups = groupQuery.findByIdIn(ids);
        if (isNotEmpty(groups)) {
          // Delete groups and related associations
          delete0(ids);

          // Record operation audit logs
          operationLogCmd.addAll(GROUP, groups, DELETED);
        }
        return null;
      }
    }.execute();
  }

  /**
   * Deletes groups and cleans up all related associations.
   *
   * <p>This method performs comprehensive cleanup including:</p>
   * <ul>
   *   <li>Deleting group configurations</li>
   *   <li>Removing group-user associations</li>
   *   <li>Cleaning up tag associations</li>
   *   <li>Removing authorization policies</li>
   * </ul>
   *
   * @param ids Set of group identifiers to delete
   */
  @Override
  public void delete0(Set<Long> ids) {
    if (isEmpty(ids)) {
      return;
    }

    // Delete group configurations
    groupRepo.deleteByIdIn(ids);

    // Clean up group associations
    userGroupCmd.deleteAllByGroupId(ids);
    orgTagTargetCmd.deleteAllByTarget(OrgTargetType.GROUP, ids);

    // Remove group authorization policies in AAS service
    authPolicyGroupCmd.groupPolicyDeleteBatch(new HashSet<>(ids), null);
  }

  /**
   * Updates group enabled/disabled status.
   *
   * <p>This method manages group status including:</p>
   * <ul>
   *   <li>Updating group enabled status</li>
   *   <li>Recording operation audit logs</li>
   *   <li>Separating enabled and disabled groups for logging</li>
   * </ul>
   *
   * @param groups List of groups to update status
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(List<Group> groups) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        // Update group status
        List<Group> groupsDb = batchUpdateOrNotFound(groups);

        // Record operation audit logs for enabled and disabled groups
        operationLogCmd.addAll(GROUP, groupsDb.stream()
            .filter(Group::getEnabled).toList(), ENABLED);
        operationLogCmd.addAll(GROUP, groupsDb.stream()
            .filter(x -> !x.getEnabled()).toList(), DISABLED);
        return null;
      }
    }.execute();
  }

  /**
   * Empties directory groups by setting directory empty flag.
   *
   * @param groupIds Set of group identifiers to empty
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void emptyDirectoryGroups(Set<Long> groupIds) {
    groupRepo.updateDirectoryEmptyByGroupIdIn(groupIds);
  }

  /**
   * Empties directory groups by directory identifier.
   *
   * @param directoryId Directory identifier
   */
  @Override
  public void emptyDirectoryGroups(Long directoryId) {
    groupRepo.updateDirectoryEmptyByDirectoryId(directoryId);
  }

  /**
   * Deletes groups by directory with optional sync deletion.
   *
   * <p>This method handles directory-based deletion including:</p>
   * <ul>
   *   <li>Full deletion with sync flag</li>
   *   <li>Empty directory groups without sync</li>
   * </ul>
   *
   * @param directoryId Directory identifier
   * @param deleteSync  Whether to perform full deletion
   */
  @Override
  public void deleteByDirectory(Long directoryId, boolean deleteSync) {
    if (deleteSync) {
      // Perform full deletion of groups
      delete0(groupRepo.findGroupIdsByDirectoryId(directoryId));
    } else {
      // Empty directory groups only
      emptyDirectoryGroups(directoryId);
    }
  }

  /**
   * Builds organization tag targets for groups.
   *
   * <p>This method converts group tag IDs to domain objects for storage.</p>
   *
   * @param groups List of groups with tag associations
   * @return List of organization tag targets
   */
  private List<OrgTagTarget> buildOrgTagTargets(List<Group> groups) {
    return groups.stream().filter(x -> isNotEmpty(x.getTagIds()))
        .map(dept -> dtoToGroupTagsDomain(dept.getTagIds(), dept.getId()))
        .flatMap(Collection::stream).toList();
  }

  @Override
  protected BaseRepository<Group, Long> getRepository() {
    return this.groupRepo;
  }
}
