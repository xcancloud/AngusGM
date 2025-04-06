package cloud.xcan.angus.core.gm.interfaces.notice.facade.internal;

import cloud.xcan.angus.api.gm.notice.dto.SendNoticeDto;
import cloud.xcan.angus.api.manager.NoticeManager;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.NoticeDoorFacade;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class NoticeDoorFacadeImpl implements NoticeDoorFacade {

  @Resource
  private NoticeManager noticeManager;

  @Override
  public void send(SendNoticeDto dto) {
    noticeManager.send(dto);
  }

}
