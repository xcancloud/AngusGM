package cloud.xcan.angus.core.gm.interfaces.notice.facade;

import cloud.xcan.angus.api.gm.notice.dto.SendNoticeDto;


public interface NoticeCombinedFacade {

  void send(SendNoticeDto dto);
}
