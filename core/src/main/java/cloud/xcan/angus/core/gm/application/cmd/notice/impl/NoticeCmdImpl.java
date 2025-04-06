package cloud.xcan.angus.core.gm.application.cmd.notice.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.notice.NoticeCmd;
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
        return insert(notice);
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(List<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        noticeRepo.deleteByIdIn(ids);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Notice, Long> getRepository() {
    return noticeRepo;
  }
}
