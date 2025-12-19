package cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler;


import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserFullName;

import cloud.xcan.angus.core.gm.domain.message.Message;
import cloud.xcan.angus.core.gm.domain.message.MessageInfo;
import cloud.xcan.angus.core.gm.domain.message.MessageStatus;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageAddDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageFindDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageDetailVo;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.time.LocalDateTime;
import java.util.Set;


public class MessageAssembler {

  public static Message messageDtoDomain(MessageAddDto dto) {
    return new Message().setTitle(dto.getTitle())
        .setContent(dto.getContent())
        .setReceiveType(dto.getReceiveType())
        .setSendType(dto.getSendType())
        .setTimingDate(dto.getTimingDate() == null ? LocalDateTime.now() : dto.getTimingDate())
        .setReceiveObjectData(dto.getReceiveObjects())
        .setReadNum(0)
        .setReceiveObjectType(dto.getReceiveObjectType()).setSentNum(0)
        .setStatus(MessageStatus.PENDING)
        .setReceiveTenantId(dto.getReceiveTenantId())
        .setDeleted(false)
        .setCreator(getUserFullName());
  }

  public static MessageDetailVo toDetailVo(Message message) {
    return new MessageDetailVo().setId(message.getId())
        .setTitle(message.getTitle())
        .setContent(message.getContent())
        .setSentType(message.getSendType())
        .setTenantId(message.getTenantId())
        .setTimingDate(message.getTimingDate())
        .setReceiveObjectType(message.getReceiveObjectType())
        .setReceiveObjects(message.getReceiveObjectData())
        .setSentNum(message.getSentNum()).setReadNum(message.getReadNum())
        .setStatus(message.getStatus())
        .setFailureReason(message.getFailureReason())
        .setReceiveType(message.getReceiveType())
        .setReceiveTenantId(message.getReceiveTenantId())
        .setCreatedBy(message.getCreatedBy())
        .setCreator(message.getCreator());
  }

  public static MessageVo toVo(MessageInfo message) {
    return new MessageVo().setId(message.getId()).setTitle(message.getTitle())
        .setSentType(message.getSendType())
        .setTenantId(message.getTenantId())
        .setTimingDate(message.getTimingDate())
        .setReceiveObjectType(message.getReceiveObjectType())
        .setSentNum(message.getSentNum()).setReadNum(message.getReadNum())
        .setStatus(message.getStatus())
        .setFailureReason(message.getFailureReason())
        .setReceiveType(message.getReceiveType())
        .setReceiveTenantId(message.getReceiveTenantId())
        .setCreatedBy(message.getCreatedBy())
        .setCreator(message.getCreator());
  }

  public static GenericSpecification<MessageInfo> getSpecification(MessageFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "timingDate")
        .orderByFields("id", "userId", "createdDate")
        .matchSearchFields("title")
        .build();
    return new GenericSpecification<>(filters);
  }

}
