package cloud.xcan.angus.core.gm.interfaces.message.facade.dto;

import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.core.gm.domain.SentType;
import cloud.xcan.angus.core.gm.domain.message.MessageReceiveType;
import cloud.xcan.angus.core.gm.domain.message.MessageStatus;
import cloud.xcan.angus.remote.PageQuery;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class MessageSearchDto extends PageQuery {

  private Long id;

  private MessageStatus status;

  private String title;

  private SentType sendType;

  private MessageReceiveType receiveType;

  private ReceiveObjectType receiveObjectType;

  private Long userId;

  private LocalDateTime createDate;

  private LocalDateTime timingDate;

  private Long receiveTenantId;

}
