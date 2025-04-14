package cloud.xcan.angus.api.gm.message.dto;


import static cloud.xcan.angus.api.commonlink.MessageCenterConstant.MAX_PUSH_CONTENT_LENGTH;
import static cloud.xcan.angus.api.commonlink.MessageCenterConstant.MAX_PUSH_OBJECT_NUM;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_OUT_ID_LENGTH;

import cloud.xcan.angus.api.enums.PushMediaType;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Valid
@Setter
@Getter
@Accessors(chain = true)
public class MessageCenterPushDto implements Serializable {

  @Schema(description = "Send messages to other instances of the service.", requiredMode = RequiredMode.REQUIRED)
  private boolean broadcast = true;

  @Length(max = MAX_OUT_ID_LENGTH)
  @Schema(description = "Out message id, used to establish associations with external businesses.",
      maxLength = MAX_OUT_ID_LENGTH)
  private String messageId;

  @NotNull
  @Schema(description = "Push message media type.", requiredMode = RequiredMode.REQUIRED)
  private PushMediaType pushMediaType;

  @Schema(description = "Send message user id.")
  private Long sendUserId;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Send message user full name.", maxLength = MAX_NAME_LENGTH)
  private String sendUserName;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Send message short title.", maxLength = MAX_NAME_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String title;

  @NotBlank
  @Length(max = MAX_PUSH_CONTENT_LENGTH)
  @Schema(description = "Send message content.", maxLength = MAX_PUSH_CONTENT_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String content;

  @NotNull
  @Schema(description = "Message recipient object type.", requiredMode = RequiredMode.REQUIRED)
  private ReceiveObjectType receiveObjectType;

  @Size(max = MAX_PUSH_OBJECT_NUM)
  @Schema(description = "Message recipient object ids, maximum support for `10000`. Note: Send to everyone when not specified.")
  private List<Long> receiveObjectIds;

}
