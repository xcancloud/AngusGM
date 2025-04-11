package cloud.xcan.angus.core.gm.interfaces.message.facade;


import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCurrentFindDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageCurrentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageCurrentVo;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageStatusCountVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;
import java.util.Set;


public interface MessageCurrentFacade {

  void delete(Set<Long> ids);

  void read(Set<Long> ids);

  MessageCurrentDetailVo detail(Long id);

  PageResult<MessageCurrentVo> list(MessageCurrentFindDto dto);

  List<MessageStatusCountVo> statusCount(Long userid);


}
