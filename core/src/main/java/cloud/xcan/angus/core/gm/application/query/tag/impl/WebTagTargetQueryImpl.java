package cloud.xcan.angus.core.gm.application.query.tag.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.gm.application.converter.AppTagTargetConverter;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagTargetQuery;
import cloud.xcan.angus.core.gm.domain.tag.WebTagRepo;
import cloud.xcan.angus.core.gm.domain.tag.WebTagTargetListRepo;
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
  private WebTagRepo webTagRepo;

  @Resource
  private WebTagTargetListRepo webTagTargetListRepo;

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
  public List<WebTag> checkAndFind(Collection<Long> ids) {
    List<WebTag> tags = webTagRepo.findAllById(ids);
    assertResourceNotFound(isNotEmpty(tags), ids.iterator().next(), "WebTag");

    if (ids.size() != tags.size()) {
      for (WebTag tag : tags) {
        assertResourceNotFound(ids.contains(tag.getId()), tag.getId(), "WebTag");
      }
    }
    return tags;
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
