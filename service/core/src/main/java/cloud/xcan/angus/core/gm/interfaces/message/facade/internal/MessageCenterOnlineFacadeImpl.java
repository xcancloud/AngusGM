package cloud.xcan.angus.core.gm.interfaces.message.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler.MessageCenterOnlineAssembler.getSearchCriteria;
import static cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler.MessageCenterOnlineAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler.MessageCenterOnlineAssembler.toVo;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.gm.domain.message.center.MessageCenterOnline;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCenterCmd;
import cloud.xcan.angus.core.gm.application.query.message.MessageCenterOnlineQuery;
import cloud.xcan.angus.core.gm.application.query.message.MessageCenterOnlineSearch;
import cloud.xcan.angus.core.gm.interfaces.message.facade.MessageCenterOnlineFacade;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOfflineDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOnlineFindDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOnlineSearchDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler.MessageCenterOnlineAssembler;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageCenterOnlineVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class MessageCenterOnlineFacadeImpl implements MessageCenterOnlineFacade {

  @Resource
  private MessageCenterOnlineQuery messageCenterOnlineQuery;

  @Resource
  private MessageCenterOnlineSearch messageCenterOnlineSearch;

  @Resource
  private MessageCenterCmd messageCenterCmd;

  @Override
  public void offline(MessageCenterOfflineDto dto) {
    messageCenterCmd.offline(dto);
  }

  @Override
  public MessageCenterOnlineVo detail(Long userId) {
    return toVo(messageCenterOnlineQuery.find(userId));
  }

  @Override
  public PageResult<MessageCenterOnlineVo> list(MessageCenterOnlineFindDto dto) {
    Page<MessageCenterOnline> page = messageCenterOnlineQuery.find(
        getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, MessageCenterOnlineAssembler::toVo);
  }

  @Override
  public PageResult<MessageCenterOnlineVo> search(MessageCenterOnlineSearchDto dto) {
    Page<MessageCenterOnline> page = messageCenterOnlineSearch
        .search(getSearchCriteria(dto), dto.tranPage(),
            MessageCenterOnline.class, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, MessageCenterOnlineAssembler::toVo);
  }

}
