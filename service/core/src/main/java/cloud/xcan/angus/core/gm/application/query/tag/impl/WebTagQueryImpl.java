package cloud.xcan.angus.core.gm.application.query.tag.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.APP_TAG_EXIST_ERROR_T;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.APP_TAG_REPEAT_ERROR_T;
import static cloud.xcan.angus.remote.message.ProtocolException.M.PARAM_ERROR_KEY;
import static cloud.xcan.angus.spec.utils.ObjectUtils.duplicateByKey;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.tag.WebTagRepo;
import cloud.xcan.angus.core.gm.domain.tag.WebTagSearchRepo;
import cloud.xcan.angus.core.gm.domain.tag.WebTagTargetRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * <p>
 * Implementation of web tag query operations.
 * </p>
 * <p>
 * Manages web tag retrieval, validation, and application association. Provides comprehensive web
 * tag querying with full-text search support.
 * </p>
 * <p>
 * Supports web tag detail retrieval, paginated listing, name validation, application association,
 * and uniqueness checking for comprehensive web tag administration.
 * </p>
 */
@org.springframework.stereotype.Service
public class WebTagQueryImpl implements WebTagQuery {

  @Resource
  private WebTagRepo webTagRepo;
  @Resource
  private WebTagSearchRepo webTagSearchRepo;
  @Resource
  private WebTagTargetRepo webTagTargetRepo;
  @Resource
  private AppQuery appQuery;

  /**
   * <p>
   * Retrieves detailed web tag information by ID.
   * </p>
   * <p>
   * Fetches complete web tag record with existence validation. Throws ResourceNotFound exception if
   * web tag does not exist.
   * </p>
   */
  @Override
  public WebTag detail(Long id) {
    return new BizTemplate<WebTag>() {

      @Override
      protected WebTag process() {
        return checkAndFind(id);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves web tags with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search and specification-based filtering. Returns paginated web tag
   * results.
   * </p>
   */
  @Override
  public Page<WebTag> list(GenericSpecification<WebTag> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<WebTag>>() {

      @Override
      protected Page<WebTag> process() {
        return fullTextSearch
            ? webTagSearchRepo.find(spec.getCriteria(), pageable, WebTag.class, match)
            : webTagRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves web tags by IDs.
   * </p>
   * <p>
   * Returns web tags for the specified tag IDs. Returns empty list if no tags found.
   * </p>
   */
  @Override
  public List<WebTag> findAllById(Collection<Long> tagIds) {
    return webTagRepo.findAllById(tagIds);
  }

  /**
   * <p>
   * Retrieves web tags by target ID.
   * </p>
   * <p>
   * Returns web tags associated with the specified target. Returns null if no tags found for
   * target.
   * </p>
   */
  @Override
  public List<WebTag> findByTargetId(Long targetId) {
    Set<Long> tagIds = webTagTargetRepo.findAllByTargetId(targetId)
        .stream().map(WebTagTarget::getTagId).collect(Collectors.toSet());
    if (isNotEmpty(tagIds)) {
      return webTagRepo.findAllById(tagIds);
    }
    return null;
  }

  /**
   * <p>
   * Retrieves web tags by multiple target IDs.
   * </p>
   * <p>
   * Returns web tags grouped by target ID for multiple targets. Associates tags with targets for
   * complete data.
   * </p>
   */
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

  /**
   * <p>
   * Validates and retrieves web tag by ID.
   * </p>
   * <p>
   * Returns web tag with existence validation. Throws ResourceNotFound if web tag does not exist.
   * </p>
   */
  @Override
  public WebTag checkAndFind(Long id) {
    return webTagRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "WebTag"));
  }

  /**
   * <p>
   * Validates and retrieves web tags by IDs.
   * </p>
   * <p>
   * Returns web tags with existence validation. Validates that all requested web tag IDs exist.
   * </p>
   */
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

  /**
   * <p>
   * Validates web tag names for addition.
   * </p>
   * <p>
   * Ensures web tag names do not already exist when adding new tags. Checks for duplicate names in
   * parameters and existing tags.
   * </p>
   */
  @Override
  public void checkAddTagNameExist(List<String> names) {
    checkRepeatedTagNameInParams(names);
    List<WebTag> webTagsDb = webTagRepo.findByNameIn(names);
    if (isNotEmpty(webTagsDb)) {
      throw ResourceExisted.of(APP_TAG_EXIST_ERROR_T, new Object[]{webTagsDb.get(0).getName()});
    }
  }

  /**
   * <p>
   * Validates web tag names for update.
   * </p>
   * <p>
   * Ensures web tag name uniqueness when updating existing tags. Allows same tag to keep its name
   * during update.
   * </p>
   */
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

  /**
   * <p>
   * Validates repeated tag names in parameters.
   * </p>
   * <p>
   * Checks for duplicate tag names within the provided name list. Throws ProtocolException if
   * duplicate names are found.
   * </p>
   */
  @Override
  public void checkRepeatedTagNameInParams(List<String> names) {
    List<String> repeatedNames = names.stream().filter(duplicateByKey(x -> x))
        .collect(Collectors.toList());
    if (isNotEmpty(repeatedNames)) {
      throw ProtocolException.of(APP_TAG_REPEAT_ERROR_T, PARAM_ERROR_KEY,
          new Object[]{repeatedNames.get(0)});
    }
  }

  /**
   * <p>
   * Sets application tags for application IDs.
   * </p>
   * <p>
   * Associates web tags with applications by application IDs. Enriches applications with tag
   * information for complete data.
   * </p>
   */
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

  /**
   * <p>
   * Sets application tags for application list.
   * </p>
   * <p>
   * Associates web tags with applications in the provided list. Enriches applications with tag
   * information for complete data.
   * </p>
   */
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
