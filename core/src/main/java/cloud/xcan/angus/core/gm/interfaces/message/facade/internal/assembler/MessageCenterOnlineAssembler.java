package cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler;

import static cloud.xcan.angus.core.gm.infra.message.MessageCenterNoticeMessage.MESSAGE_CENTER_SIGN_OUT;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserFullname;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;

import cloud.xcan.angus.api.commonlink.mcenter.MessageCenterOnline;
import cloud.xcan.angus.api.enums.PushMediaType;
import cloud.xcan.angus.core.gm.infra.message.MessageCenterNotice;
import cloud.xcan.angus.core.gm.infra.message.MessageCenterNoticeMessage;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOfflineDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOnlineFindDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOnlineSearchDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageCenterOnlineVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;

public class MessageCenterOnlineAssembler {

  public static MessageCenterOnlineVo toVo(MessageCenterOnline online) {
    MessageCenterOnlineVo vo = new MessageCenterOnlineVo();
    BeanUtils.copyProperties(online, vo); // TODO
    return vo;
  }

  public static MessageCenterNoticeMessage dtoToOfflineDomain(MessageCenterOfflineDto dto) {
    return MessageCenterNoticeMessage.newBuilder()
        .id(UUID.randomUUID().toString())
        .bizKey(MESSAGE_CENTER_SIGN_OUT)
        .pushMediaType(PushMediaType.PLAIN_TEXT)
        .receiveObjectType(dto.getReceiveObjectType())
        .content(new MessageCenterNotice().setSendUserId(getUserId())
            .setSendUserName(getUserFullname())
            .setSendDate(new Date()).setReceiveObjectIds(dto.getReceiveObjectIds()))
        .build();
  }

  public static Specification<MessageCenterOnline> getSpecification(
      MessageCenterOnlineFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("userId")
        .matchSearchFields("fullName", "remoteAddress")
        .orderByFields("id", "userId")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static Set<SearchCriteria> getSearchCriteria(MessageCenterOnlineSearchDto dto) {
    // Build the final filters
    return new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("userId")
        .matchSearchFields("fullName", "remoteAddress")
        .orderByFields("id", "userId")
        .build();
  }

}
