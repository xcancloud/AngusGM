package cloud.xcan.angus.core.gm.application.cmd.notice.impl;

import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.NOTICE;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.notice.NoticeCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.query.notice.NoticeQuery;
import cloud.xcan.angus.core.gm.domain.notice.Notice;
import cloud.xcan.angus.core.gm.domain.notice.NoticeRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class NoticeCmdImpl extends CommCmd<Notice, Long> implements NoticeCmd {

  @Resource
  private NoticeRepo noticeRepo;

  @Resource
  private NoticeQuery noticeQuery;

  @Resource
  private OperationLogCmd operationLogCmd;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(Notice notice) {
    return new BizTemplate<IdKey<Long, Object>>() {
      @Override
      protected void checkParams() {
        noticeQuery.checkAppSendTimingParam(notice);
      }

      @Override
      protected IdKey<Long, Object> process() {
        IdKey<Long, Object> idKey = insert(notice);
        operationLogCmd.add(NOTICE, notice, CREATED);
        return idKey;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(List<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        List<Notice> notices = noticeRepo.findAllById(ids);
        if (isNotEmpty(notices)){
          noticeRepo.deleteByIdIn(ids);
          operationLogCmd.addAll(NOTICE, notices, DELETED);
        }
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Notice, Long> getRepository() {
    return noticeRepo;
  }
}
