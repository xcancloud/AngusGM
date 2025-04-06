package cloud.xcan.angus.core.gm.application.query.tag.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.APP_TAG_EXIST_ERROR_T;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.APP_TAG_REPEAT_ERROR_T;
import static cloud.xcan.angus.remote.message.CommProtocolException.M.PARAM_ERROR_KEY;
import static cloud.xcan.angus.spec.utils.ObjectUtils.duplicateByKey;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.tag.WebTagRepo;
import cloud.xcan.angus.core.gm.domain.tag.WebTagTargetRepo;
import cloud.xcan.angus.remote.message.CommProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


@Biz
public class WebTagQueryImpl implements WebTagQuery {

  @Resource
  private WebTagRepo webTagRepo;

  @Resource
  private WebTagTargetRepo webTagTargetRepo;

  @Resource
  private AppQuery appQuery;

  @Override
  public WebTag detail(Long id) {
    return new BizTemplate<WebTag>() {

      @Override
      protected WebTag process() {
        return checkAndFind(id);
      }
    }.execute();
  }

  @Override
  public Page<WebTag> find(Specification<WebTag> spec, Pageable pageable) {
    return new BizTemplate<Page<WebTag>>() {

      @Override
      protected Page<WebTag> process() {
        return webTagRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public List<WebTag> findAllById(Collection<Long> tagIds) {
    return webTagRepo.findAllById(tagIds);
  }

  @Override
  public List<WebTag> findByTargetId(Long targetId) {
    Set<Long> tagIds = webTagTargetRepo.findAllByTargetId(targetId).stream()
        .map(WebTagTarget::getTagId).collect(Collectors.toSet());
    if (isNotEmpty(tagIds)) {
      return webTagRepo.findAllById(tagIds);
    }
    return null;
  }

  @Override
  public Map<Long, List<WebTag>> findByTargetIdIn(Collection<Long> targetIds) {
    List<WebTagTarget> tagTargets = webTagTargetRepo.findAllByTargetIdIn(targetIds);
    if (isEmpty(tagTargets)) {
      return null;
    }
    Set<Long> tagIds = tagTargets.stream().map(WebTagTarget::getTagId).collect(Collectors.toSet());
    if (isEmpty(tagIds)) {
      return null;
    }
    Map<Long, WebTag> appTagMap = webTagRepo.findAllById(tagIds).stream()
        .collect(Collectors.toMap(WebTag::getId, x -> x));
    for (WebTagTarget tagTarget : tagTargets) {
      tagTarget.setTag(appTagMap.get(tagTarget.getTagId()));
    }
    return tagTargets.stream().collect(Collectors.groupingBy(WebTagTarget::getTargetId,
        Collectors.mapping(WebTagTarget::getTag, Collectors.toList())));
  }

  @Override
  public WebTag checkAndFind(Long id) {
    return webTagRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "WebTag"));
  }

  @Override
  public List<WebTag> checkAndFind(Collection<Long> tagIds) {
    List<WebTag> tags = webTagRepo.findByIdIn(tagIds);
    assertResourceNotFound(isNotEmpty(tags), tagIds.iterator().next(), "WebTag");

    if (tagIds.size() != tags.size()) {
      for (WebTag tag : tags) {
        assertResourceNotFound(tagIds.contains(tag.getId()), tag.getId(), "WebTag");
      }
    }
    return tags;
  }

  @Override
  public void checkAddTagNameExist(List<String> names) {
    checkRepeatedTagNameInParams(names);
    List<WebTag> webTagsDb = webTagRepo.findByNameIn(names);
    if (isNotEmpty(webTagsDb)) {
      throw ResourceExisted.of(APP_TAG_EXIST_ERROR_T, new Object[]{webTagsDb.get(0).getName()});
    }
  }

  @Override
  public void checkUpdateTagNameExist(List<WebTag> webTags) {
    List<String> names = webTags.stream().map(WebTag::getName).collect(Collectors.toList());
    checkRepeatedTagNameInParams(names);
    List<WebTag> webTagsDb = webTagRepo.findByNameIn(names);
    if (isNotEmpty(webTagsDb)) {
      webTags.forEach(appTag -> {
        webTagsDb.forEach(appTagDb -> {
          if (appTag.getName().equals(appTagDb.getName())
              && !appTag.getId().equals(appTagDb.getId())) {
            throw ResourceExisted.of(APP_TAG_EXIST_ERROR_T, new Object[]{appTag.getName()});
          }
        });
      });
    }
  }

  @Override
  public void checkRepeatedTagNameInParams(List<String> names) {
    List<String> repeatedNames = names.stream().filter(duplicateByKey(x -> x))
        .collect(Collectors.toList());
    if (isNotEmpty(repeatedNames)) {
      throw CommProtocolException.of(APP_TAG_REPEAT_ERROR_T, PARAM_ERROR_KEY,
          new Object[]{repeatedNames.get(0)});
    }
  }

  @Override
  public void setAppTags(Collection<Long> appIds) {
    if (isEmpty(appIds)) {
      return;
    }
    List<App> apps = appQuery.checkAndFind(appIds, false);
    Map<Long, List<WebTag>> tagMap = findByTargetIdIn(appIds);
    if (isNotEmpty(tagMap)) {
      for (App app : apps) {
        app.setTags(tagMap.get(app.getId()));
      }
    }
  }

  @Override
  public void setAppTags(List<App> apps) {
    if (isEmpty(apps)) {
      return;
    }
    Map<Long, List<WebTag>> tagMap = findByTargetIdIn(apps.stream().map(App::getId)
        .collect(Collectors.toSet()));
    if (isNotEmpty(tagMap)) {
      for (App app : apps) {
        app.setTags(tagMap.get(app.getId()));
      }
    }
  }
}
