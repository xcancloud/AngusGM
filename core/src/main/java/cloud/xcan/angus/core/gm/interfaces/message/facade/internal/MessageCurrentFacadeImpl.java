package cloud.xcan.angus.core.gm.interfaces.message.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler.MessageCurrentAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler.MessageCurrentAssembler.toDetailVo;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCurrentCmd;
import cloud.xcan.angus.core.gm.application.query.message.MessageCurrentQuery;
import cloud.xcan.angus.core.gm.domain.message.MessageSent;
import cloud.xcan.angus.core.gm.interfaces.message.facade.MessageCurrentFacade;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCurrentFindDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler.MessageCurrentAssembler;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageCurrentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageCurrentVo;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageStatusCountVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class MessageCurrentFacadeImpl implements MessageCurrentFacade {

  @Resource
  private MessageCurrentCmd messageCurrentCmd;

  @Resource
  private MessageCurrentQuery messageQuery;

  @Override
  public void delete(Set<Long> ids) {
    messageCurrentCmd.delete(ids);
  }

  @Override
  public void read(Set<Long> ids) {
    messageCurrentCmd.read(ids);
  }

  @NameJoin
  @Override
  public MessageCurrentDetailVo detail(Long id) {
    MessageSent messageSent = messageQuery.detail(id);
    return toDetailVo(messageSent);
  }

  @NameJoin
  @Override
  public PageResult<MessageCurrentVo> list(MessageCurrentFindDto dto) {
    Page<MessageSent> page = messageQuery.list(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, MessageCurrentAssembler::toVo);
  }

  @Override
  public List<MessageStatusCountVo> statusCount(Long userId) {
    return messageQuery.statusCount(nullSafe(userId, getUserId()));
  }
}
