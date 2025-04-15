package cloud.xcan.angus.core.gm.application.cmd.tag.impl;

import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.ORG_TAG;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATED;

import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.tag.WebTagCmd;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagQuery;
import cloud.xcan.angus.core.gm.domain.tag.WebTagRepo;
import cloud.xcan.angus.core.gm.domain.tag.WebTagTargetRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;


@Biz
public class WebTagCmdImpl extends CommCmd<WebTag, Long> implements WebTagCmd {

  @Resource
  private WebTagRepo webTagRepo;

  @Resource
  private WebTagQuery webTagQuery;

  @Resource
  private WebTagTargetRepo webTagTargetRepo;

  @Resource
  private OperationLogCmd operationLogCmd;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<WebTag> tags) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      @Override
      protected void checkParams() {
        // Check the tag names existed
        webTagQuery.checkAddTagNameExist(tags.stream().map(WebTag::getName)
            .collect(Collectors.toList()));
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        List<IdKey<Long, Object>> idKeys = batchInsert(tags, "name");
        operationLogCmd.addAll(ORG_TAG, tags, CREATED);
        return idKeys;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(List<WebTag> tags) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Check the tag names existed
        webTagQuery.checkUpdateTagNameExist(tags);
      }

      @Override
      protected Void process() {
        List<WebTag> tagsDb =  batchUpdateOrNotFound(tags);
        operationLogCmd.addAll(ORG_TAG, tagsDb, UPDATED);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(HashSet<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        List<WebTag> tagsDb = webTagQuery.checkAndFind(ids);
        if (!tagsDb.isEmpty()) {
          webTagTargetRepo.deleteByTagIdIn(ids);
          webTagRepo.deleteByIdIn(ids);

          operationLogCmd.addAll(ORG_TAG, tagsDb, DELETED);
        }
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<WebTag, Long> getRepository() {
    return this.webTagRepo;
  }
}
