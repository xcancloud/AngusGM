package cloud.xcan.angus.core.gm.application.query.tag.impl;

import static cloud.xcan.angus.api.commonlink.UCConstant.TOP_TENANT_ADMIN;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.tag.OrgTag;
import cloud.xcan.angus.api.commonlink.tag.OrgTagRepo;
import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tag.OrgTagTargetRepo;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.manager.SettingTenantQuotaManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.gm.application.converter.OrgTagTargetConverter;
import cloud.xcan.angus.core.gm.application.query.dept.DeptQuery;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.application.query.tag.OrgTagTargetQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.tag.OrgTagTargetListRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * <p>
 * Implementation of organization tag target query operations.
 * </p>
 * <p>
 * Manages organization tag target retrieval, validation, and quota management.
 * Provides comprehensive organization tag target querying with target association support.
 * </p>
 * <p>
 * Supports organization tag target queries, target tag queries, quota validation,
 * deduplication, and target association for comprehensive organization tag target administration.
 * </p>
 */
@Biz
@SummaryQueryRegister(name = "OrgTagTarget", table = "org_tag_target", topAuthority = TOP_TENANT_ADMIN,
    groupByColumns = {"created_date", "target_type"})
public class OrgTagTargetQueryImpl implements OrgTagTargetQuery {

  @Resource
  private OrgTagTargetRepo orgTagTargetRepo;
  @Resource
  private OrgTagRepo orgTagRepo;
  @Resource
  private OrgTagTargetListRepo orgTagTargetListRepo;
  @Resource
  private SettingTenantQuotaManager settingTenantQuotaManager;
  @Resource
  private UserQuery userQuery;
  @Resource
  private DeptQuery deptQuery;
  @Resource
  private GroupQuery groupQuery;

  /**
   * <p>
   * Retrieves tag targets for specific organization tag.
   * </p>
   * <p>
   * Queries targets associated with the specified organization tag.
   * Validates required parameters and returns paginated results.
   * </p>
   */
  @Override
  public Page<OrgTagTarget> findTagTarget(GenericSpecification<OrgTagTarget> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<OrgTagTarget>>(true, true) {
      @Override
      protected void checkParams() {
        String targetType = findFirstValue(spec.getCriteria(), "targetType");
        ProtocolAssert.assertNotEmpty(targetType, "targetType is required");
        String tagId = findFirstValue(spec.getCriteria(), "tagId");
        ProtocolAssert.assertNotEmpty(tagId, "tagId is required");
      }

      @Override
      protected Page<OrgTagTarget> process() {
        return orgTagTargetListRepo.find(spec.getCriteria(), pageable, OrgTagTarget.class,
            OrgTagTargetConverter::objectArrToOrgTagTarget, null);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves target tags for specific target.
   * </p>
   * <p>
   * Queries organization tags associated with the specified target.
   * Validates required parameters and returns paginated results.
   * </p>
   */
  @Override
  public Page<OrgTagTarget> findTargetTag(GenericSpecification<OrgTagTarget> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<OrgTagTarget>>(true, true) {
      @Override
      protected void checkParams() {
        String targetType = findFirstValue(spec.getCriteria(), "targetType");
        ProtocolAssert.assertNotEmpty(targetType, "targetType is required");
        String targetId = findFirstValue(spec.getCriteria(), "targetId");
        ProtocolAssert.assertNotEmpty(targetId, "targetId is required");
      }

      @Override
      protected Page<OrgTagTarget> process() {
        return orgTagTargetListRepo.find(spec.getCriteria(), pageable, OrgTagTarget.class,
            OrgTagTargetConverter::objectArrToOrgTagTarget, null);
      }
    }.execute();
  }

  /**
   * <p>
   * Validates target tag quota for replacement.
   * </p>
   * <p>
   * Checks if replacing target tags would exceed quota limits.
   * Validates quota for specific organization ID.
   * </p>
   */
  @Override
  public void checkTargetTagQuota(Long optTenantId, long incr, Long orgId) {
    if (incr > 0) {
      // long num = orgTargetTagRepo.countByTenantIdAndTargetId(tenantId, orgId); <- Replace org tags
      settingTenantQuotaManager
          .checkTenantQuota(QuotaResource.OrgTargetTag, Collections.singleton(orgId), /*num +*/
              incr);
    }
  }

  /**
   * <p>
   * Validates target append tag quota.
   * </p>
   * <p>
   * Checks if appending target tags would exceed quota limits.
   * Validates quota for specific organization ID.
   * </p>
   */
  @Override
  public void checkTargetAppendTagQuota(Long optTenantId, long incr, Long orgId) {
    if (incr > 0) {
      long num = orgTagTargetRepo.countByTenantIdAndTargetId(optTenantId, orgId);
      settingTenantQuotaManager.checkTenantQuota(QuotaResource.OrgTargetTag,
          Collections.singleton(orgId), num + incr);
    }
  }

  /**
   * <p>
   * Validates target tag quota for multiple targets.
   * </p>
   * <p>
   * Checks quota for replacement operations across multiple targets.
   * Groups targets by target ID for efficient quota checking.
   * </p>
   */
  @Override
  public void checkTargetTagQuota(Long optTenantId, List<OrgTagTarget> tagTargets) {
    if (isEmpty(tagTargets)) {
      return;
    }
    Map<Long, List<OrgTagTarget>> tagTargetMap = tagTargets.stream()
        .collect(Collectors.groupingBy(OrgTagTarget::getTargetId));
    for (Long targetId : tagTargetMap.keySet()) {
      checkTargetTagQuota(optTenantId, tagTargetMap.get(targetId).size(), targetId);
    }
  }

  /**
   * <p>
   * Validates target append tag quota for multiple targets.
   * </p>
   * <p>
   * Checks quota for append operations across multiple targets.
   * Groups targets by target ID for efficient quota checking.
   * </p>
   */
  @Override
  public void checkTargetAppendTagQuota(Long optTenantId, List<OrgTagTarget> tagTargets) {
    if (isEmpty(tagTargets)) {
      return;
    }
    Map<Long, List<OrgTagTarget>> tagTargetMap = tagTargets.stream()
        .collect(Collectors.groupingBy(OrgTagTarget::getTargetId));
    for (Long targetId : tagTargetMap.keySet()) {
      checkTargetAppendTagQuota(optTenantId, tagTargetMap.get(targetId).size(), targetId);
    }
  }

  /**
   * <p>
   * Retrieves all organization tag targets for specific target.
   * </p>
   * <p>
   * Returns organization tag targets with tag associations.
   * Enriches results with tag information for complete data.
   * </p>
   */
  @Override
  public List<OrgTagTarget> findAllByTarget(OrgTargetType targetType, Long targetId) {
    List<OrgTagTarget> tagTargets = orgTagTargetRepo
        .findAllByTargetTypeAndTargetId(targetType, targetId);
    if (isNotEmpty(tagTargets)) {
      List<OrgTag> tags = orgTagRepo.findAllById(tagTargets.stream().map(OrgTagTarget::getTagId)
          .collect(Collectors.toList()));
      if (isNotEmpty(tags)) {
        Map<Long, OrgTag> tagMap = tags.stream().collect(Collectors.toMap(OrgTag::getId, x -> x));
        for (OrgTagTarget tagTarget : tagTargets) {
          tagTarget.setTag(tagMap.get(tagTarget.getTagId()));
        }
      }
    }
    return tagTargets;
  }

  /**
   * <p>
   * Validates users and performs deduplication.
   * </p>
   * <p>
   * Checks user existence and removes duplicate tag targets.
   * Returns validated user list for tag target operations.
   * </p>
   */
  @Override
  public List<User> checkUserAndDeduplication(Set<OrgTagTarget> newTagTargets,
      List<OrgTagTarget> tagTargets, Long tagId) {
    Set<OrgTagTarget> userTags = tagTargets.stream()
        .filter(x -> x.getTargetType().equals(OrgTargetType.USER)).collect(Collectors.toSet());
    List<User> users = null;
    if (isNotEmpty(userTags)) {
      Set<Long> userIds = userTags.stream().map(OrgTagTarget::getTargetId)
          .collect(Collectors.toSet());
      users = userQuery.checkAndFind(userIds);
      Set<OrgTagTarget> tagsDb = orgTagTargetRepo.findByTagIdAndTargetTypeAndTargetIdIn(
          tagId, OrgTargetType.USER, userIds);
      if (isNotEmpty(tagsDb)) {
        newTagTargets.removeAll(tagsDb);
      }
    }
    return users;
  }

  /**
   * <p>
   * Validates departments and performs deduplication.
   * </p>
   * <p>
   * Checks department existence and removes duplicate tag targets.
   * Returns validated department list for tag target operations.
   * </p>
   */
  @Override
  public List<Dept> checkDeptAndDeduplication(Set<OrgTagTarget> newTagTargets,
      List<OrgTagTarget> tagTargets, Long tagId) {
    Set<OrgTagTarget> deptTags = tagTargets.stream()
        .filter(x -> x.getTargetType().equals(OrgTargetType.DEPT)).collect(Collectors.toSet());
    List<Dept> dept = null;
    if (isNotEmpty(deptTags)) {
      Set<Long> deptIds = deptTags.stream().map(OrgTagTarget::getTargetId)
          .collect(Collectors.toSet());
      dept = deptQuery.checkAndFind(deptIds);
      Set<OrgTagTarget> tagsDb = orgTagTargetRepo.findByTagIdAndTargetTypeAndTargetIdIn(
          tagId, OrgTargetType.DEPT, deptIds);
      if (isNotEmpty(tagsDb)) {
        newTagTargets.removeAll(tagsDb);
      }
    }
    return dept;
  }

  /**
   * <p>
   * Validates groups and performs deduplication.
   * </p>
   * <p>
   * Checks group existence and removes duplicate tag targets.
   * Returns validated group list for tag target operations.
   * </p>
   */
  @Override
  public List<Group> checkGroupAndDeduplication(Set<OrgTagTarget> newTagTargets,
      List<OrgTagTarget> tagTargets, Long tagId) {
    Set<OrgTagTarget> groupTags = tagTargets.stream()
        .filter(x -> x.getTargetType().equals(OrgTargetType.GROUP)).collect(Collectors.toSet());
    List<Group> groups = null;
    if (isNotEmpty(groupTags)) {
      Set<Long> groupIds = groupTags.stream().map(OrgTagTarget::getTargetId)
          .collect(Collectors.toSet());
      groups = groupQuery.checkValidAndFind(groupIds);
      Set<OrgTagTarget> tagsDb = orgTagTargetRepo.findByTagIdAndTargetTypeAndTargetIdIn(
          tagId, OrgTargetType.GROUP, groupIds);
      if (isNotEmpty(tagsDb)) {
        newTagTargets.removeAll(tagsDb);
      }
    }
    return groups;
  }

}
