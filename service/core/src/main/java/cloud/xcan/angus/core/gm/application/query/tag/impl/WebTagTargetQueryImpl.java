package cloud.xcan.angus.core.gm.application.query.tag.impl;

import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Collections.emptyList;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTargetType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.gm.application.converter.AppTagTargetConverter;
import cloud.xcan.angus.core.gm.application.query.app.AppFuncQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagTargetQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.tag.WebTagTargetListRepo;
import cloud.xcan.angus.core.gm.domain.tag.WebTagTargetRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.JpaSort;

@Biz
public class WebTagTargetQueryImpl implements WebTagTargetQuery {

  @Resource
  private WebTagTargetRepo webTagTargetRepo;

  @Resource
  private WebTagTargetListRepo webTagTargetListRepo;

  @Resource
  private AppQuery appQuery;

  @Resource
  private AppFuncQuery appFuncQuery;

  @Override
  public Page<WebTagTarget> findTagTarget(GenericSpecification<WebTagTarget> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<WebTagTarget>>() {
      @Override
      protected void checkParams() {
        String targetType = findFirstValue(spec.getCriteria(), "targetType");
        ProtocolAssert.assertNotEmpty(targetType, "targetType is required");
        String tagId = findFirstValue(spec.getCriteria(), "tagId");
        ProtocolAssert.assertNotEmpty(tagId, "tagId is required");
      }

      @Override
      protected Page<WebTagTarget> process() {
        return webTagTargetListRepo.find(spec.getCriteria(), pageable, WebTagTarget.class,
            AppTagTargetConverter::objectArrToAppTagTarget, null);
      }
    }.execute();
  }

  @Override
  public Page<WebTagTarget> findTargetTag(GenericSpecification<WebTagTarget> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<WebTagTarget>>() {
      @Override
      protected void checkParams() {
        String targetType = findFirstValue(spec.getCriteria(), "targetType");
        ProtocolAssert.assertNotEmpty(targetType, "targetType is required");
        String targetId = findFirstValue(spec.getCriteria(), "targetId");
        ProtocolAssert.assertNotEmpty(targetId, "targetId is required");
      }

      @Override
      protected Page<WebTagTarget> process() {
        return webTagTargetListRepo.find(spec.getCriteria(), pageable, WebTagTarget.class,
            AppTagTargetConverter::objectArrToAppTagTarget, null);
      }
    }.execute();
  }

  @Override
  public List<App> checkAppAndDeduplication(Set<WebTagTarget> newTagTargets,
      List<WebTagTarget> tagTargets, Long tagId) {
    Set<WebTagTarget> userTags = tagTargets.stream()
        .filter(x -> x.getTargetType().equals(WebTagTargetType.APP)).collect(Collectors.toSet());
    List<App> appsDb = emptyList();
    if (isNotEmpty(userTags)) {
      List<Long> appIds = userTags.stream().map(WebTagTarget::getTargetId)
          .collect(Collectors.toList());
      appsDb = appQuery.checkAndFind(appIds, false);
      Set<WebTagTarget> tagsDb = webTagTargetRepo.findByTagIdAndTargetTypeAndTargetIdIn(
          tagId, WebTagTargetType.APP, appIds);
      if (isNotEmpty(tagsDb)) {
        newTagTargets.removeAll(tagsDb);
      }
    }
    return appsDb;
  }

  @Override
  public List<AppFunc> checkAppFuncAndDeduplication(Set<WebTagTarget> newTagTargets,
      List<WebTagTarget> tagTargets, Long tagId) {
    Set<WebTagTarget> deptTags = tagTargets.stream()
        .filter(x -> !x.getTargetType().equals(WebTagTargetType.APP)).collect(Collectors.toSet());
    List<AppFunc> appFuncDb = emptyList();
    if (isNotEmpty(deptTags)) {
      Set<Long> funcIds = deptTags.stream().map(WebTagTarget::getTargetId)
          .collect(Collectors.toSet());
      appFuncDb = appFuncQuery.checkAndFind(funcIds, false);
      Set<WebTagTarget> tagsDb = webTagTargetRepo.findByTagIdAndTargetTypeNotAndTargetIdIn(
          tagId, WebTagTargetType.APP, funcIds);
      if (isNotEmpty(tagsDb)) {
        newTagTargets.removeAll(tagsDb);
      }
    }
    return appFuncDb;
  }

  @Override
  public Map<Long, List<WebTagTarget>> findTargetTagByTargetId(Collection<Long> targetIds) {
    Set<SearchCriteria> criteria = new HashSet<>();
    criteria.add(SearchCriteria.in("targetId", targetIds));
    //criteria.add(SearchCriteria.equal("targetType", !WebTagTargetType.APP.getValue()));
    Page<WebTagTarget> page = webTagTargetListRepo.find(criteria, PageRequest.of(0, 5000,
            JpaSort.by(Order.asc("id"))), WebTagTarget.class,
        AppTagTargetConverter::objectArrToAppTagTarget, null);
    if (page.hasContent()) {
      return page.getContent().stream().collect(Collectors.groupingBy(WebTagTarget::getTargetId));
    }
    return null;
  }
}
