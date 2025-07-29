package cloud.xcan.angus.core.gm.interfaces.message.facade.dto;

import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.core.gm.domain.SentType;
import cloud.xcan.angus.core.gm.domain.message.MessageReceiveType;
import cloud.xcan.angus.core.gm.domain.message.MessageStatus;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MessageFindDto extends PageQuery {

  @Schema(description = "Message identifier")
  private Long id;

  @Schema(description = "Message delivery status")
  private MessageStatus status;

  @Schema(description = "Message title for filtering")
  private String title;

  @Schema(description = "Message delivery timing configuration")
  private SentType sendType;

  @Schema(description = "Message reception type for delivery method")
  private MessageReceiveType receiveType;

  @Schema(description = "Recipient object type for message targeting")
  private ReceiveObjectType receiveObjectType;

  @Schema(description = "User identifier for message filtering")
  private Long userId;

  @Schema(description = "Message creation timestamp")
  private LocalDateTime createDate;

  @Schema(description = "Scheduled delivery timestamp")
  private LocalDateTime timingDate;

  @Schema(description = "Target tenant identifier for message delivery")
  private Long receiveTenantId;

}
