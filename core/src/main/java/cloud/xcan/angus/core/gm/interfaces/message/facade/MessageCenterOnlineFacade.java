package cloud.xcan.angus.core.gm.interfaces.message.facade;

import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOfflineDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOnlineFindDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOnlineSearchDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageCenterOnlineVo;
import cloud.xcan.angus.remote.PageResult;

public interface MessageCenterOnlineFacade {

  void offline(MessageCenterOfflineDto dto);

  MessageCenterOnlineVo detail(Long userId);

  PageResult<MessageCenterOnlineVo> list(MessageCenterOnlineFindDto dto);

  PageResult<MessageCenterOnlineVo> search(MessageCenterOnlineSearchDto dto);

}
