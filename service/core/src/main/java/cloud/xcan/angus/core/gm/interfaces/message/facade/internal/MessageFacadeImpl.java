package cloud.xcan.angus.core.gm.interfaces.message.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler.MessageAssembler.getSearchCriteria;
import static cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler.MessageAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler.MessageAssembler.messageDtoDomain;
import static cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler.MessageAssembler.toDetailVo;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCmd;
import cloud.xcan.angus.core.gm.application.query.message.MessageQuery;
import cloud.xcan.angus.core.gm.application.query.message.MessageSearch;
import cloud.xcan.angus.core.gm.domain.message.Message;
import cloud.xcan.angus.core.gm.domain.message.MessageInfo;
import cloud.xcan.angus.core.gm.interfaces.message.facade.MessageFacade;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageAddDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageFindDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageSearchDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler.MessageAssembler;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageDetailVo;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class MessageFacadeImpl implements MessageFacade {

  @Resource
  private MessageCmd messageCmd;

  @Resource
  private MessageQuery messageQuery;

  @Resource
  private MessageSearch messageSearch;

  @Override
  public IdKey<Long, Object> add(MessageAddDto dto) {
    return messageCmd.add(messageDtoDomain(dto));
  }

  @Override
  public void delete(Set<Long> ids) {
    messageCmd.delete(ids);
  }

  @NameJoin
  @Override
  public MessageDetailVo detail(Long id) {
    Message message = messageQuery.findById(id);
    return toDetailVo(message);
  }

  @NameJoin
  @Override
  public PageResult<MessageVo> list(MessageFindDto dto) {
    Page<MessageInfo> page = messageQuery.find(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, MessageAssembler::toVo);
  }

  @NameJoin
  @Override
  public PageResult<MessageVo> search(MessageSearchDto dto) {
    Page<MessageInfo> page = messageSearch.search(getSearchCriteria(dto), dto.tranPage(),
        MessageInfo.class, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, MessageAssembler::toVo);
  }

}
