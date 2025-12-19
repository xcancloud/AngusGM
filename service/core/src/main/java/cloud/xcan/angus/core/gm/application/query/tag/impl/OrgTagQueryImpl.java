package cloud.xcan.angus.core.gm.application.query.tag.impl;

import static cloud.xcan.angus.api.commonlink.UCConstant.TOP_TENANT_ADMIN;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.remote.message.http.ResourceExisted.M.RESOURCE_ALREADY_EXISTS_T;
import static cloud.xcan.angus.spec.utils.ObjectUtils.duplicateByKey;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.tag.OrgTag;
import cloud.xcan.angus.api.commonlink.tag.OrgTagRepo;
import cloud.xcan.angus.api.manager.SettingTenantQuotaManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.tag.OrgTagQuery;
import cloud.xcan.angus.core.gm.domain.tag.OrgTagSearchRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.utils.ObjectUtils;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * <p>
 * Implementation of organization tag query operations.
 * </p>
 * <p>
 * Manages organization tag retrieval, validation, and quota management. Provides comprehensive
 * organization tag querying with full-text search and summary support.
 * </p>
 * <p>
 * Supports organization tag detail retrieval, paginated listing, name validation, quota management,
 * and uniqueness checking for comprehensive organization tag administration.
 * </p>
 */
@org.springframework.stereotype.Service
@SummaryQueryRegister(name = "OrgTag", table = "org_tag", topAuthority = TOP_TENANT_ADMIN,
    groupByColumns = {"created_date"})
public class OrgTagQueryImpl implements OrgTagQuery {

  @Resource
  private OrgTagRepo orgTagRepo;
  @Resource
  private OrgTagSearchRepo orgTagSearchRepo;
  @Resource
  private SettingTenantQuotaManager settingTenantQuotaManager;

  /**
   * <p>
   * Retrieves detailed organization tag information by ID.
   * </p>
   * <p>
   * Fetches complete organization tag record with existence validation. Throws ResourceNotFound
   * exception if organization tag does not exist.
   * </p>
   */
  @Override
  public OrgTag detail(Long id) {
    return new BizTemplate<OrgTag>(true, true) {

      @Override
      protected OrgTag process() {
        return checkAndFind(id);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves organization tags with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search and specification-based filtering. Returns paginated organization tag
   * results.
   * </p>
   */
  @Override
  public Page<OrgTag> list(GenericSpecification<OrgTag> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<OrgTag>>(true, true) {

      @Override
      protected Page<OrgTag> process() {
        return fullTextSearch
            ? orgTagSearchRepo.find(spec.getCriteria(), pageable, OrgTag.class, match)
            : orgTagRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  /**
   * <p>
   * Validates and retrieves organization tag by ID.
   * </p>
   * <p>
   * Returns organization tag with existence validation. Throws ResourceNotFound if organization tag
   * does not exist.
   * </p>
   */
  @Override
  public OrgTag checkAndFind(Long id) {
    return orgTagRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "OrgTag"));
  }

  /**
   * <p>
   * Validates and retrieves organization tags by IDs.
   * </p>
   * <p>
   * Returns organization tags with existence validation. Validates that all requested organization
   * tag IDs exist.
   * </p>
   */
  @Override
  public List<OrgTag> checkAndFind(Collection<Long> ids) {
    List<OrgTag> tags = orgTagRepo.findAllById(ids);
    assertResourceNotFound(isNotEmpty(tags), ids.iterator().next(), "OrgTag");

    if (ids.size() != tags.size()) {
      for (OrgTag tag : tags) {
        assertResourceNotFound(ids.contains(tag.getId()), tag.getId(), "OrgTag");
      }
    }
    return tags;
  }

  /**
   * <p>
   * Validates organization tag names in parameters.
   * </p>
   * <p>
   * Checks for duplicate tag names within the provided tag list. Throws ResourceExisted if
   * duplicate names are found.
   * </p>
   */
  @Override
  public void checkNameInParam(List<OrgTag> tags) {
    List<String> repeatTagNames = tags.stream().filter(duplicateByKey(OrgTag::getName))
        .map(OrgTag::getName).collect(Collectors.toList());
    assertTrue(isEmpty(repeatTagNames), RESOURCE_ALREADY_EXISTS_T,
        new Object[]{repeatTagNames, "Name"});
  }

  /**
   * <p>
   * Validates organization tag names for addition.
   * </p>
   * <p>
   * Ensures organization tag names do not already exist when adding new tags. Throws
   * ResourceExisted if tag names already exist.
   * </p>
   */
  @Override
  public void checkAddTagName(Long tenantId, List<OrgTag> orgTags) {
    List<OrgTag> deptDbs = orgTagRepo.findAllByTenantIdAndNameIn(tenantId, orgTags.stream()
        .filter(ObjectUtils::isNotEmpty).map(OrgTag::getName).collect(Collectors.toSet()));
    assertResourceExisted(isEmpty(deptDbs), isNotEmpty(deptDbs) ? deptDbs.get(0).getName() : null,
        "OrgTag");
  }

  /**
   * <p>
   * Validates organization tag names for update.
   * </p>
   * <p>
   * Ensures organization tag name uniqueness when updating existing tags. Allows same tag to keep
   * its name during update.
   * </p>
   */
  @Override
  public void checkUpdateTagName(Long tenantId, List<OrgTag> orgTags) {
    if (isEmpty(orgTags)) {
      return;
    }
    List<OrgTag> orgTagDbs = orgTagRepo.findAllByTenantIdAndNameIn(tenantId, orgTags.stream()
        .filter(ObjectUtils::isNotEmpty).map(OrgTag::getName).collect(Collectors.toSet()));
    if (isNotEmpty(orgTagDbs)) {
      Map<String, List<OrgTag>> codeGroupsMap = orgTagDbs.stream()
          .collect(Collectors.groupingBy(OrgTag::getName));
      for (OrgTag orgTag : orgTags) {
        if (isNotEmpty(orgTag.getName())) {
          List<OrgTag> nameTags = codeGroupsMap.get(orgTag.getName());
          assertResourceExisted(isEmpty(nameTags)
                  || (nameTags.size() == 1 && orgTag.getId().equals(nameTags.get(0).getId())),
              orgTagDbs.get(0).getName(), "OrgTag");
        }
      }
    }
  }

  /**
   * <p>
   * Validates organization tag quota for tenant.
   * </p>
   * <p>
   * Checks if adding organization tags would exceed tenant quota limits. Throws appropriate
   * exception if quota would be exceeded.
   * </p>
   */
  @Override
  public void checkQuota(Long optTenantId, long incr) {
    if (incr > 0) {
      long num = orgTagRepo.countByTenantId(optTenantId);
      settingTenantQuotaManager.checkTenantQuota(QuotaResource.OrgTag, null, num + incr);
    }
  }
}
