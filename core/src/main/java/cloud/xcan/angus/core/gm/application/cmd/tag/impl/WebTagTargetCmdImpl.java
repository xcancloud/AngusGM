package cloud.xcan.angus.core.gm.application.cmd.tag.impl;

import static cloud.xcan.angus.core.gm.application.converter.AppTagTargetConverter.toAppTagTarget;
import static cloud.xcan.angus.core.gm.application.converter.AppTagTargetConverter.toFuncTagTarget;
import static cloud.xcan.angus.core.gm.application.converter.WebTagConverter.assembleAppOrFuncTags;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Collections.singleton;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTargetType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.tag.WebTagTargetCmd;
import cloud.xcan.angus.core.gm.application.query.app.AppFuncQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagQuery;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagTargetQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.tag.WebTagTargetRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import com.google.common.collect.Sets;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;


@Biz
public class WebTagTargetCmdImpl extends CommCmd<WebTagTarget, Long> implements WebTagTargetCmd {

  @Resource
  private WebTagTargetRepo webTagTargetRepo;

  @Resource
  private WebTagTargetQuery webTagTargetQuery;

  @Resource
  private AppQuery appQuery;

  @Resource
  private AppFuncQuery appFuncQuery;

  @Resource
  private WebTagQuery webTagQuery;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> tagTargetAdd(Long tagId, List<WebTagTarget> tagTargets) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      @Override
      protected void checkParams() {
        webTagQuery.checkAndFind(tagId);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        Set<WebTagTarget> newTagTargets = new HashSet<>(tagTargets);
        checkAppAndDeduplication(newTagTargets, tagTargets, tagId);
        checkAppFuncAndDeduplication(newTagTargets, tagTargets, tagId);

        if (isNotEmpty(newTagTargets)) {
          return batchInsert(newTagTargets);
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void tagTargetDelete(Long tagId, HashSet<Long> targetIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        webTagQuery.checkAndFind(tagId);
      }

      @Override
      protected Void process() {
        webTagTargetRepo.deleteByTagIdAndTargetIdIn(tagId, targetIds);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> appTagAdd(Long appId, LinkedHashSet<Long> tagIds) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {

      @Override
      protected void checkParams() {
        appQuery.checkAndFind(appId, false);
        webTagTargetQuery.checkAndFind(tagIds);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        Set<WebTagTarget> tagsDb = webTagTargetRepo
            .findByTagIdInAndTargetTypeAndTargetId(tagIds, WebTagTargetType.APP, appId);
        if (isNotEmpty(tagsDb)) {
          tagIds.removeAll(tagsDb.stream().map(WebTagTarget::getTagId).toList());
        }
        if (isNotEmpty(tagIds)) {
          webTagTargetRepo.batchInsert(tagIds.stream()
              .map(x -> new WebTagTarget().setTagId(x)
                  .setTargetType(WebTagTargetType.APP).setTargetId(appId))
              .collect(Collectors.toList()));
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void appTagReplace(Long appId, LinkedHashSet<Long> tagIds) {
    new BizTemplate<Void>() {
      App appDb;

      @Override
      protected void checkParams() {
        appDb = appQuery.checkAndFind(appId, false);
      }

      @Override
      protected Void process() {
        // Clear empty
        deleteAllByTarget(WebTagTargetType.APP, singleton(appId));
        // Save new association
        if (isNotEmpty(tagIds)) {
          add(tagIds.stream().map(tagId -> toAppTagTarget(tagId, appDb))
              .collect(Collectors.toList()));
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void appTagDelete(Long appId, HashSet<Long> tagIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        appQuery.checkAndFind(appId, false);
      }

      @Override
      protected Void process() {
        webTagTargetRepo.deleteByTagIdInAndTargetId(tagIds, appId);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> funcTagAdd(Long funcId, LinkedHashSet<Long> tagIds) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      AppFunc appFuncDb;

      @Override
      protected void checkParams() {
        appFuncDb = appFuncQuery.checkAndFind(funcId, false);
        webTagTargetQuery.checkAndFind(tagIds);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        Set<WebTagTarget> tagsDb = webTagTargetRepo.findByTagIdInAndTargetTypeNotAndTargetId(
            tagIds, WebTagTargetType.APP, funcId);
        if (isNotEmpty(tagsDb)) {
          tagIds.removeAll(tagsDb.stream().map(WebTagTarget::getTagId).toList());
        }
        if (isNotEmpty(tagIds)) {
          webTagTargetRepo.batchInsert(tagIds.stream()
              .map(x -> new WebTagTarget().setTagId(x)
                  .setTargetType(appFuncDb.getType().toTagTargetType()).setTargetId(funcId))
              .collect(Collectors.toList()));
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void funcTagReplace(Long funcId, LinkedHashSet<Long> tagIds) {
    new BizTemplate<Void>() {
      AppFunc appFuncDb;

      @Override
      protected void checkParams() {
        appFuncDb = appFuncQuery.checkAndFind(funcId, false);
      }

      @Override
      protected Void process() {
        // Clear empty
        deleteAllByTargetNot(WebTagTargetType.APP, singleton(funcId));
        // Save new association
        if (isNotEmpty(tagIds)) {
          add(tagIds.stream().map(tagId -> toFuncTagTarget(tagId, appFuncDb))
              .collect(Collectors.toList()));
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void funcTagDelete(Long funcId, HashSet<Long> tagIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        appFuncQuery.checkAndFind(funcId, false);
      }

      @Override
      protected Void process() {
        webTagTargetRepo.deleteByTagIdInAndTargetId(tagIds, funcId);
        return null;
      }
    }.execute();
  }

  @Override
  public void add(List<WebTagTarget> tagTargets) {
    // Check the tags existed
    webTagQuery.checkAndFind(tagTargets.stream().map(WebTagTarget::getTagId)
        .collect(Collectors.toSet()));
    batchInsert0(tagTargets);
  }

  @Override
  public void tag(WebTagTargetType targetType, Long targetId, Set<Long> tagIds) {
    if (isNull(targetId) || isEmpty(tagIds)) {
      return;
    }
    // Check and find application
    // appQuery.find(targetId); // Check by outer method
    // Check and find tags
    webTagQuery.checkAndFind(tagIds);

    // Delete tag targets
    delete(Sets.newHashSet(targetId));
    // Build tags targets
    add(assembleAppOrFuncTags(targetId, targetType, tagIds));
  }

  @Override
  public void delete(Collection<Long> targetIds) {
    webTagTargetRepo.deleteByTargetIdIn(targetIds);
  }

  @Override
  public void deleteAllByTarget(WebTagTargetType targetType, Collection<Long> targetIds) {
    webTagTargetRepo.deleteAllByTargetTypeAndTargetIdIn(targetType, targetIds);
  }

  @Override
  public void deleteAllByTargetNot(WebTagTargetType targetType, Collection<Long> targetIds) {
    webTagTargetRepo.deleteAllByTargetTypeNotAndTargetIdIn(targetType, targetIds);
  }

  private void checkAppAndDeduplication(Set<WebTagTarget> newTagTargets,
      List<WebTagTarget> tagTargets, Long tagId) {
    Set<WebTagTarget> userTags = tagTargets.stream()
        .filter(x -> x.getTargetType().equals(WebTagTargetType.APP)).collect(Collectors.toSet());
    if (isNotEmpty(userTags)) {
      List<Long> appIds = userTags.stream().map(WebTagTarget::getTargetId)
          .collect(Collectors.toList());
      appQuery.checkAndFind(appIds, false);
      Set<WebTagTarget> tagsDb = webTagTargetRepo.findByTagIdAndTargetTypeAndTargetIdIn(
          tagId, WebTagTargetType.APP, appIds);
      if (isNotEmpty(tagsDb)) {
        newTagTargets.removeAll(tagsDb);
      }
    }
  }

  private void checkAppFuncAndDeduplication(Set<WebTagTarget> newTagTargets,
      List<WebTagTarget> tagTargets, Long tagId) {
    Set<WebTagTarget> deptTags = tagTargets.stream()
        .filter(x -> !x.getTargetType().equals(WebTagTargetType.APP)).collect(Collectors.toSet());
    if (isNotEmpty(deptTags)) {
      Set<Long> funcIds = deptTags.stream().map(WebTagTarget::getTargetId)
          .collect(Collectors.toSet());
      appFuncQuery.checkAndFind(funcIds, false);
      Set<WebTagTarget> tagsDb = webTagTargetRepo.findByTagIdAndTargetTypeNotAndTargetIdIn(
          tagId, WebTagTargetType.APP, funcIds);
      if (isNotEmpty(tagsDb)) {
        newTagTargets.removeAll(tagsDb);
      }
    }
  }

  @Override
  protected BaseRepository<WebTagTarget, Long> getRepository() {
    return this.webTagTargetRepo;
  }
}
