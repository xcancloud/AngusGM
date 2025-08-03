package cloud.xcan.angus.api.gm.message.dto;


import static cloud.xcan.angus.api.commonlink.MessageCenterConstant.MAX_PUSH_CONTENT_LENGTH;
import static cloud.xcan.angus.api.commonlink.MessageCenterConstant.MAX_PUSH_OBJECT_NUM;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_OUT_ID_LENGTH;

import cloud.xcan.angus.api.enums.PushMediaType;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class MessageCenterPushDto implements Serializable {

  @Schema(description = "Flag indicating whether to broadcast message to other service instances. Used for cross-instance message distribution", requiredMode = RequiredMode.REQUIRED)
  private boolean broadcast = true;

  @Length(max = MAX_OUT_ID_LENGTH)
  @Schema(description = "External message identifier for establishing associations with external systems. Used for cross-system integration and tracking")
  private String messageId;

  @NotNull
  @Schema(description = "Push message media type for determining message format and delivery method", requiredMode = RequiredMode.REQUIRED)
  private PushMediaType mediaType;

  @Schema(description = "User identifier who sent the message. Used for sender identification and audit tracking")
  private Long sendBy;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Full name of the user who sent the message. Used for sender display and identification")
  private String sendByName;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Short title for the push message. Used for message identification and display", requiredMode = RequiredMode.REQUIRED)
  private String title;

  @NotBlank
  @Length(max = MAX_PUSH_CONTENT_LENGTH)
  @Schema(description = "Push message content for recipient notification. Used for message delivery and display", requiredMode = RequiredMode.REQUIRED)
  private String content;

  @NotNull
  @Schema(description = "Message recipient object type for system-based recipient selection. Used for recipient identification and targeting", requiredMode = RequiredMode.REQUIRED)
  private ReceiveObjectType receiveObjectType;

  @Size(max = MAX_PUSH_OBJECT_NUM)
  @Schema(description = "Message recipient object identifiers for system-based recipient selection. Sends to everyone when not specified", type = "array")
  private List<Long> receiveObjectIds;

}
