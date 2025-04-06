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
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.tag.OrgTagQuery;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.utils.ObjectUtils;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


@Biz
@SummaryQueryRegister(name = "OrgTag", table = "org_tag", topAuthority = TOP_TENANT_ADMIN,
    groupByColumns = {"created_date"})
public class OrgTagQueryImpl implements OrgTagQuery {

  @Resource
  private OrgTagRepo orgTagRepo;

  @Resource
  private SettingTenantQuotaManager settingTenantQuotaManager;

  @Override
  public OrgTag detail(Long id) {
    return new BizTemplate<OrgTag>(true, true) {

      @Override
      protected OrgTag process() {
        return checkAndFind(id);
      }
    }.execute();
  }

  @Override
  public Page<OrgTag> list(Specification<OrgTag> spec, Pageable pageable) {
    return new BizTemplate<Page<OrgTag>>(true, true) {

      @Override
      protected Page<OrgTag> process() {
        return orgTagRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public OrgTag checkAndFind(Long id) {
    return orgTagRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "OrgTag"));
  }

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

  @Override
  public void checkNameInParam(List<OrgTag> tags) {
    List<String> repeatTagNames = tags.stream().filter(duplicateByKey(OrgTag::getName))
        .map(OrgTag::getName).collect(Collectors.toList());
    assertTrue(isEmpty(repeatTagNames), RESOURCE_ALREADY_EXISTS_T,
        new Object[]{repeatTagNames, "Name"});
  }

  @Override
  public void checkAddTagName(Long tenantId, List<OrgTag> orgTags) {
    List<OrgTag> deptDbs = orgTagRepo.findAllByTenantIdAndNameIn(tenantId, orgTags.stream()
        .filter(ObjectUtils::isNotEmpty).map(OrgTag::getName).collect(Collectors.toSet()));
    assertResourceExisted(isEmpty(deptDbs), isNotEmpty(deptDbs) ? deptDbs.get(0).getName() : null,
        "OrgTag");
  }

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

  @Override
  public void checkQuota(Long optTenantId, long incr) {
    if (incr > 0) {
      long num = orgTagRepo.countByTenantId(optTenantId);
      settingTenantQuotaManager.checkTenantQuota(QuotaResource.OrgTag, null, num + incr);
    }
  }
}
