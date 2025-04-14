package cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.mcenter.MessageCenterOnline;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOnlineFindDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOnlineSearchDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageCenterOnlineVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;

public class MessageCenterOnlineAssembler {

  public static MessageCenterOnlineVo toVo(MessageCenterOnline online) {
    return new MessageCenterOnlineVo()
        .setUserId(online.getUserId())
        .setFullName(online.getFullName())
        .setUserAgent(online.getUserAgent())
        .setDeviceId(online.getDeviceId())
        .setRemoteAddress(online.getRemoteAddress())
        .setOnline(online.getOnline())
        .setOnlineDate(online.getOnlineDate())
        .setOfflineDate(online.getOfflineDate());
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
