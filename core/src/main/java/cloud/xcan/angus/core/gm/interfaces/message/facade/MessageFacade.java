package cloud.xcan.angus.core.gm.interfaces.message.facade;


import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageAddDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageFindDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageSearchDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageDetailVo;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.Set;


public interface MessageFacade {

  IdKey<Long, Object> add(MessageAddDto dto);

  void delete(Set<Long> ids);

  PageResult<MessageVo> list(MessageFindDto dto);

  MessageDetailVo detail(Long id);

  PageResult<MessageVo> search(MessageSearchDto dto);

}
