package cloud.xcan.angus.core.gm.application.query.tag.impl;

import static cloud.xcan.angus.api.commonlink.UCConstant.TOP_TENANT_ADMIN;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.tag.OrgTag;
import cloud.xcan.angus.api.commonlink.tag.OrgTagRepo;
import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tag.OrgTagTargetRepo;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.manager.SettingTenantQuotaManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.gm.application.converter.OrgTagTargetConverter;
import cloud.xcan.angus.core.gm.application.query.tag.OrgTagTargetQuery;
import cloud.xcan.angus.core.gm.domain.tag.OrgTagTargetListRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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

  @Override
  public void checkTargetTagQuota(Long optTenantId, long incr, Long orgId) {
    if (incr > 0) {
      // long num = orgTargetTagRepo.countByTenantIdAndTargetId(tenantId, orgId); <- Replace org tags
      settingTenantQuotaManager
          .checkTenantQuota(QuotaResource.OrgTargetTag, Collections.singleton(orgId), /*num +*/
              incr);
    }
  }

  @Override
  public void checkTargetAppendTagQuota(Long optTenantId, long incr, Long orgId) {
    if (incr > 0) {
      long num = orgTagTargetRepo.countByTenantIdAndTargetId(optTenantId, orgId);
      settingTenantQuotaManager.checkTenantQuota(QuotaResource.OrgTargetTag,
          Collections.singleton(orgId), num + incr);
    }
  }

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

}
