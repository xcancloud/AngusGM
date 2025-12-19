package cloud.xcan.angus.core.gm.application.query.group.impl;

import static cloud.xcan.angus.api.commonlink.UCConstant.TOP_TENANT_ADMIN;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.group.GroupRepo;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.commonlink.user.group.GroupUserNum;
import cloud.xcan.angus.api.manager.GroupManager;
import cloud.xcan.angus.api.manager.SettingTenantQuotaManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.application.query.group.GroupUserQuery;
import cloud.xcan.angus.core.gm.application.query.tag.OrgTagTargetQuery;
import cloud.xcan.angus.core.gm.domain.group.GroupListRepo;
import cloud.xcan.angus.core.gm.domain.group.GroupSearchRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.utils.ObjectUtils;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * <p>
 * Implementation of group query operations.
 * </p>
 * <p>
 * Manages group retrieval, validation, and user count association. Provides comprehensive group
 * querying with full-text search and summary support.
 * </p>
 * <p>
 * Supports group detail retrieval, user count association, quota validation, and code uniqueness
 * checking for comprehensive group management.
 * </p>
 */
@org.springframework.stereotype.Service
@SummaryQueryRegister(name = "Group", table = "group0", topAuthority = TOP_TENANT_ADMIN,
    groupByColumns = {"created_date", "enabled", "source"})
public class GroupQueryImpl implements GroupQuery {

  @Resource
  private GroupRepo groupRepo;
  @Resource
  private GroupListRepo groupListRepo;
  @Resource
  private GroupSearchRepo groupSearchRepo;
  @Resource
  private GroupManager groupManager;
  @Resource
  private GroupUserQuery userGroupQuery;
  @Resource
  private SettingTenantQuotaManager settingTenantQuotaManager;
  @Resource
  private OrgTagTargetQuery orgTagTargetQuery;

  /**
   * <p>
   * Retrieves detailed group information with associated tags and user count.
   * </p>
   * <p>
   * Fetches complete group record with tag associations and user count. Throws ResourceNotFound
   * exception if group does not exist.
   * </p>
   */
  @Override
  public Group detail(Long id) {
    return new BizTemplate<Group>(true, true) {
      @Override
      protected Group process() {
        Group groupDb = groupRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Group"));
        groupDb.setTags(orgTagTargetQuery.findAllByTarget(OrgTargetType.GROUP, id));
        setUserNum(Collections.singletonList(groupDb));
        return groupDb;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves groups with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search and specification-based filtering. Sets user count for comprehensive
   * group management.
   * </p>
   */
  @Override
  public Page<Group> list(GenericSpecification<Group> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Group>>(true, true) {
      @Override
      protected Page<Group> process() {
        Page<Group> page = fullTextSearch
            ? groupSearchRepo.find(spec.getCriteria(), pageable, Group.class, match)
            : groupListRepo.find(spec.getCriteria(), pageable, Group.class, null);
        if (page.hasContent()) {
          setUserNum(page.getContent());
        }
        return page;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves groups by IDs without validation.
   * </p>
   * <p>
   * Returns groups for the specified IDs without validation checks.
   * </p>
   */
  @Override
  public List<Group> findByIdIn(Collection<Long> ids) {
    return groupRepo.findByIdIn(ids);
  }

  /**
   * <p>
   * Validates and retrieves group by ID.
   * </p>
   * <p>
   * Verifies group exists and returns group information. Throws appropriate exception if group does
   * not exist.
   * </p>
   */
  @Override
  public Group checkAndFind(Long id) {
    return groupManager.checkAndFind(id);
  }

  /**
   * <p>
   * Validates and retrieves multiple groups by IDs.
   * </p>
   * <p>
   * Verifies all groups exist and returns group information. Throws appropriate exceptions for
   * missing groups.
   * </p>
   */
  @Override
  public List<Group> checkAndFind(Collection<Long> ids) {
    return groupManager.checkAndFind(ids);
  }

  /**
   * <p>
   * Validates and retrieves valid group by ID.
   * </p>
   * <p>
   * Verifies group exists and is valid, returns group information. Throws appropriate exception if
   * group does not exist or is invalid.
   * </p>
   */
  @Override
  public Group checkValidAndFind(Long id) {
    return groupManager.checkValidAndFind(id);
  }

  /**
   * <p>
   * Validates and retrieves multiple valid groups by IDs.
   * </p>
   * <p>
   * Verifies all groups exist and are valid, returns group information. Throws appropriate
   * exceptions for missing or invalid groups.
   * </p>
   */
  @Override
  public List<Group> checkValidAndFind(Collection<Long> ids) {
    return groupManager.checkValidAndFind(ids);
  }

  /**
   * <p>
   * Validates group code uniqueness for new groups.
   * </p>
   * <p>
   * Checks if group codes already exist within the tenant. Throws ResourceExisted exception if
   * codes are not unique.
   * </p>
   */
  @Override
  public void checkAddGroupCode(Long tenantId, List<Group> groups) {
    List<Group> groupDbs = groupRepo.findAllByTenantIdAndCodeIn(tenantId, groups.stream()
        .filter(ObjectUtils::isNotEmpty).map(Group::getCode).collect(Collectors.toSet()));
    assertResourceExisted(isEmpty(groupDbs), isNotEmpty(groupDbs)
        ? groupDbs.get(0).getCode() : null, "Group");
  }

  /**
   * <p>
   * Validates group code uniqueness for updated groups.
   * </p>
   * <p>
   * Checks if group codes conflict with existing groups. Allows same code for the same group during
   * updates.
   * </p>
   */
  @Override
  public void checkUpdateDeptCode(Long tenantId, List<Group> groups) {
    if (isEmpty(groups)) {
      return;
    }
    List<Group> groupDbs = groupRepo.findAllByTenantIdAndCodeIn(tenantId, groups.stream()
        .filter(ObjectUtils::isNotEmpty).map(Group::getCode).collect(Collectors.toSet()));
    if (isNotEmpty(groupDbs)) {
      Map<String, List<Group>> codeGroupsMap = groupDbs.stream()
          .collect(Collectors.groupingBy(Group::getCode));
      for (Group group : groups) {
        if (isNotEmpty(group.getCode())) {
          List<Group> codeGroups = codeGroupsMap.get(group.getCode());
          assertResourceExisted(isEmpty(codeGroups)
                  || (codeGroups.size() == 1 && group.getId().equals(codeGroups.get(0).getId())),
              groupDbs.get(0).getCode(), "Group");
        }
      }
    }
  }

  /**
   * <p>
   * Validates group quota for tenant.
   * </p>
   * <p>
   * Checks if adding groups would exceed tenant quota limits. Throws appropriate exception if quota
   * would be exceeded.
   * </p>
   */
  @Override
  public void checkQuota(Long tenantId, long incr) {
    if (incr > 0) {
      long num = groupRepo.countByTenantId(tenantId);
      settingTenantQuotaManager.checkTenantQuota(QuotaResource.Group, null, num + incr);
    }
  }

  /**
   * <p>
   * Sets user count for group list.
   * </p>
   * <p>
   * Associates user count information with groups for display purposes.
   * </p>
   */
  @Override
  public void setUserNum(List<Group> groups) {
    List<GroupUserNum> userNums = userGroupQuery
        .userCount(groups.stream().map(Group::getId).collect(Collectors.toSet()));
    if (isNotEmpty(userNums)) {
      Map<Long, GroupUserNum> userNumsMap = userNums.stream().collect(
          Collectors.toMap(GroupUserNum::getGroupId, x -> x));
      for (Group group : groups) {
        if (nonNull(userNumsMap.get(group.getId()))) {
          group.setUserNum(userNumsMap.get(group.getId()).getUserNum());
        }
      }
    }
  }
}
