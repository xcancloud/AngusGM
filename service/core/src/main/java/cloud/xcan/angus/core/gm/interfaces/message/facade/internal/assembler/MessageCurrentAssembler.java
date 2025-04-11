package cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler;


import cloud.xcan.angus.core.gm.domain.message.Message;
import cloud.xcan.angus.core.gm.domain.message.MessageSent;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCurrentFindDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageCurrentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageCurrentVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;


public class MessageCurrentAssembler {

  public static MessageCurrentDetailVo toDetailVo(MessageSent sent) {
    Message message = sent.getMessage();
    MessageCurrentDetailVo vo = new MessageCurrentDetailVo();
    vo.setId(sent.getId());
    vo.setMessageId(sent.getMessageId());
    vo.setContent(message.getContent());
    vo.setTitle(message.getTitle());
    vo.setRead(sent.getRead());
    vo.setUserId(message.getCreatedBy());
    vo.setSendDate(sent.getSentDate());
    return vo;
  }

  public static MessageCurrentVo toVo(MessageSent sent) {
    MessageCurrentVo vo = new MessageCurrentVo();
    vo.setId(sent.getId());
    vo.setMessageId(sent.getMessageId());
    vo.setTitle(Objects.isNull(sent.getMessageInfo()) ? "" : sent.getMessageInfo().getTitle());
    vo.setRead(sent.getRead());
    vo.setUserId(
        Objects.isNull(sent.getMessageInfo()) ? null : sent.getMessageInfo().getCreatedBy());
    vo.setSendDate(sent.getSentDate());
    return vo;
  }

  public static Specification<MessageSent> getSpecification(MessageCurrentFindDto dto) {
    // Build the final filters
    dto.setReceiveUserId(PrincipalContext.getUserId());
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "receiveUserId")
        .orderByFields("id", "receiveUserId", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }
}
