package cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.message.center.MessageCenterOnline;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOnlineFindDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageCenterOnlineVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class MessageCenterOnlineAssembler {

  public static MessageCenterOnlineVo toVo(MessageCenterOnline online) {
    return new MessageCenterOnlineVo()
        .setId(online.getId())
        .setUserId(online.getUserId())
        .setFullName(online.getFullName())
        .setAvatar(online.getAvatar())
        .setUserAgent(online.getUserAgent())
        .setDeviceId(online.getDeviceId())
        .setRemoteAddress(online.getRemoteAddress())
        .setOnline(online.getOnline())
        .setOnlineDate(online.getOnlineDate())
        .setOfflineDate(online.getOfflineDate());
  }

  public static GenericSpecification<MessageCenterOnline> getSpecification(
      MessageCenterOnlineFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("userId", "onlineDate", "offlineDate")
        .matchSearchFields("fullName", "remoteAddress")
        .orderByFields("id", "userId", "fullName", "onlineDate", "offlineDate")
        .build();
    return new GenericSpecification<>(filters);
  }

}
