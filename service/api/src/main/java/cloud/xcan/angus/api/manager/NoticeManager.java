package cloud.xcan.angus.api.manager;


import cloud.xcan.angus.api.gm.notice.dto.SendNoticeDto;

public interface NoticeManager {

  void send(SendNoticeDto dto);
}
