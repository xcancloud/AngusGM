package cloud.xcan.angus.core.gm.application.cmd.tag.impl;

import static cloud.xcan.angus.core.gm.application.converter.AppTagTargetConverter.toAppTagTarget;
import static cloud.xcan.angus.core.gm.application.converter.AppTagTargetConverter.toFuncTagTarget;
import static cloud.xcan.angus.core.gm.application.converter.OperationLogConverter.toOperations;
import static cloud.xcan.angus.core.gm.application.converter.WebTagConverter.assembleAppOrFuncTags;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.APP;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.APP_FUNC;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.TARGET_TAG_DELETED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.TARGET_TAG_UPDATED;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Collections.singleton;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTargetType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
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

  @Resource
  private OperationLogCmd operationLogCmd;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> tagTargetAdd(Long tagId, List<WebTagTarget> tagTargets) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      WebTag tagDb;
      List<App> appsDb;
      List<AppFunc> appFuncDb;
      Set<WebTagTarget> newTagTargets;

      @Override
      protected void checkParams() {
        // Check the tag existed
        tagDb = webTagQuery.checkAndFind(tagId);
        // Check the tag targets existed
        newTagTargets = new HashSet<>(tagTargets);
        appsDb = webTagTargetQuery.checkAppAndDeduplication(newTagTargets, tagTargets, tagId);
        appFuncDb = webTagTargetQuery.checkAppFuncAndDeduplication(newTagTargets, tagTargets,
            tagId);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        List<IdKey<Long, Object>> idKeys = batchInsert(newTagTargets);

        operationLogCmd.addAll(APP, appsDb, TARGET_TAG_UPDATED, tagDb.getName());
        operationLogCmd.addAll(APP_FUNC, appFuncDb, TARGET_TAG_UPDATED, tagDb.getName());
        return idKeys;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void tagTargetDelete(Long tagId, HashSet<Long> targetIds) {
    new BizTemplate<Void>() {
      WebTag tagDb;

      @Override
      protected void checkParams() {
        // Check the tag existed
        tagDb = webTagQuery.checkAndFind(tagId);
      }

      @Override
      protected Void process() {
        webTagTargetRepo.deleteByTagIdAndTargetIdIn(tagId, targetIds);

        List<App> appsDb = appQuery.findById(targetIds);
        operationLogCmd.addAll(APP, appsDb, TARGET_TAG_DELETED, tagDb.getName());
        List<AppFunc> appFuncDb = appFuncQuery.findById(targetIds);
        operationLogCmd.addAll(APP_FUNC, appFuncDb, TARGET_TAG_DELETED, tagDb.getName());
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> appTagAdd(Long appId, LinkedHashSet<Long> tagIds) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      App appDb;
      List<WebTag> tagsDb;

      @Override
      protected void checkParams() {
        appDb = appQuery.checkAndFind(appId, false);
        tagsDb = webTagQuery.checkAndFind(tagIds);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        Set<WebTagTarget> tagsTargetDb = webTagTargetRepo
            .findByTagIdInAndTargetTypeAndTargetId(tagIds, WebTagTargetType.APP, appId);
        if (isNotEmpty(tagsDb)) {
          tagIds.removeAll(tagsTargetDb.stream().map(WebTagTarget::getTagId).toList());
        }

        if (isNotEmpty(tagIds)) {
          webTagTargetRepo.batchInsert(tagIds.stream()
              .map(x -> new WebTagTarget().setTagId(x)
                  .setTargetType(WebTagTargetType.APP).setTargetId(appId))
              .collect(Collectors.toList()));

          operationLogCmd.add(APP, appDb, TARGET_TAG_UPDATED,
              tagsDb.stream().map(WebTag::getName).toList().toArray());
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
      App appDb;
      List<WebTag> tagsDb;

      @Override
      protected void checkParams() {
        appDb = appQuery.checkAndFind(appId, false);
        tagsDb = webTagQuery.checkAndFind(tagIds);
      }

      @Override
      protected Void process() {
        webTagTargetRepo.deleteByTagIdInAndTargetId(tagIds, appId);

        operationLogCmd.add(APP, appDb, TARGET_TAG_DELETED,
            tagsDb.stream().map(WebTag::getName).toList().toArray());
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> funcTagAdd(Long funcId, LinkedHashSet<Long> tagIds) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      AppFunc appFuncDb;
      List<WebTag> tagsDb;

      @Override
      protected void checkParams() {
        appFuncDb = appFuncQuery.checkAndFind(funcId, false);
        tagsDb = webTagQuery.checkAndFind(tagIds);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        Set<WebTagTarget> tagsTargetDb = webTagTargetRepo.findByTagIdInAndTargetTypeNotAndTargetId(
            tagIds, WebTagTargetType.APP, funcId);
        if (isNotEmpty(tagsTargetDb)) {
          tagIds.removeAll(tagsTargetDb.stream().map(WebTagTarget::getTagId).toList());
        }

        if (isNotEmpty(tagIds)) {
          webTagTargetRepo.batchInsert(tagIds.stream()
              .map(x -> new WebTagTarget().setTagId(x)
                  .setTargetType(appFuncDb.getType().toTagTargetType()).setTargetId(funcId))
              .collect(Collectors.toList()));

          operationLogCmd.add(APP_FUNC, appFuncDb, TARGET_TAG_UPDATED,
              tagsDb.stream().map(WebTag::getName).toList().toArray());
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
      AppFunc appFuncDb;
      List<WebTag> tagsDb;

      @Override
      protected void checkParams() {
        appFuncDb = appFuncQuery.checkAndFind(funcId, false);
        tagsDb = webTagQuery.checkAndFind(tagIds);
      }

      @Override
      protected Void process() {
        webTagTargetRepo.deleteByTagIdInAndTargetId(tagIds, funcId);

        operationLogCmd.add(APP_FUNC, appFuncDb, TARGET_TAG_DELETED,
            tagsDb.stream().map(WebTag::getName).toList().toArray());
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

  @Override
  protected BaseRepository<WebTagTarget, Long> getRepository() {
    return this.webTagTargetRepo;
  }
}
