package cloud.xcan.angus.core.gm.interfaces.event.facade.vo.template;

import cloud.xcan.angus.api.enums.NoticeType;
import cloud.xcan.angus.api.enums.ReceiverType;
import cloud.xcan.angus.api.pojo.UserName;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class EventTemplateReceiverInfoVo {

  private Long id;

  /**
   * At least one of receiverTypes and receiverIds is required, notification will also be sent when
   * both exist.
   */
  private Set<ReceiverType> receiverTypes;

  /**
   * At least one of receiverTypes and receiverIds is required, notification will also be sent when
   * both exist.
   */
  private Set<UserName> receivers;

  private Set<NoticeType> noticeTypes;

}
