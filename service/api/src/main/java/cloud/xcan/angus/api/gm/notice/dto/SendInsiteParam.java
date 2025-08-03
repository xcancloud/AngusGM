package cloud.xcan.angus.api.gm.notice.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.commonlink.MessageCenterConstant;
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
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@ToString
@Accessors(chain = true)
public class SendInsiteParam implements Serializable {

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "External business identifier for establishing associations with external systems. Generates UUID when not specified")
  private String messageId;

  @NotNull
  @Schema(description = "Push message media type for determining message format and delivery method", requiredMode = RequiredMode.REQUIRED)
  private PushMediaType pushMediaType;

  @Schema(description = "User identifier who sent the message. Used for sender identification and audit tracking")
  private Long sendUserId;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Full name of the user who sent the message. Used for sender display and identification")
  private String sendUserName;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Short title for the push message. Used for message identification and display", requiredMode = RequiredMode.REQUIRED)
  private String title;

  @NotBlank
  @Length(max = MessageCenterConstant.MAX_PUSH_CONTENT_LENGTH)
  @Schema(description = "Push message content for recipient notification. Used for message delivery and display", requiredMode = RequiredMode.REQUIRED)
  private String content;

  @NotNull
  @Schema(description = "Message recipient object type for system-based recipient selection. Used for recipient identification and targeting", requiredMode = RequiredMode.REQUIRED)
  private ReceiveObjectType receiveObjectType;

  @Size(max = MessageCenterConstant.MAX_PUSH_OBJECT_NUM)
  @Schema(description = "Message recipient object identifiers for system-based recipient selection")
  private List<Long> receiveObjectIds;

  public SendInsiteParam(Builder builder) {
    setMessageId(builder.messageId);
    setPushMediaType(builder.pushMediaType);
    setSendUserId(builder.sendUserId);
    setSendUserName(builder.sendUserName);
    setTitle(builder.title);
    setContent(builder.content);
    setReceiveObjectType(builder.receiveObjectType);
    setReceiveObjectIds(builder.receiveObjectIds);
  }

  public SendInsiteParam() {
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private @Length(max = MAX_CODE_LENGTH) String messageId;
    private @NotNull PushMediaType pushMediaType;
    private Long sendUserId;
    private @Length(max = MAX_NAME_LENGTH) String sendUserName;
    private @NotBlank
    @Length(max = MAX_NAME_LENGTH) String title;
    private @NotBlank
    @Length(max = MessageCenterConstant.MAX_PUSH_CONTENT_LENGTH) String content;
    private @NotNull ReceiveObjectType receiveObjectType;
    private @Size(max = MessageCenterConstant.MAX_PUSH_OBJECT_NUM) List<Long> receiveObjectIds;

    private Builder() {
    }

    public Builder messageId(@Length(max = MAX_CODE_LENGTH) String messageId) {
      this.messageId = messageId;
      return this;
    }

    public Builder pushMediaType(@NotNull PushMediaType pushMediaType) {
      this.pushMediaType = pushMediaType;
      return this;
    }

    public Builder sendUserId(Long sendUserId) {
      this.sendUserId = sendUserId;
      return this;
    }

    public Builder sendUserName(String sendUserName) {
      this.sendUserName = sendUserName;
      return this;
    }

    public Builder title(@NotBlank @Length(max = MAX_NAME_LENGTH) String title) {
      this.title = title;
      return this;
    }

    public Builder content(
        @NotBlank @Length(max = MessageCenterConstant.MAX_PUSH_CONTENT_LENGTH) String content) {
      this.content = content;
      return this;
    }

    public Builder receiveObjectType(@NotNull ReceiveObjectType receiveObjectType) {
      this.receiveObjectType = receiveObjectType;
      return this;
    }

    public Builder receiveObjectIds(
        @Size(max = MessageCenterConstant.MAX_PUSH_OBJECT_NUM) List<Long> receiveObjectIds) {
      this.receiveObjectIds = receiveObjectIds;
      return this;
    }

    public SendInsiteParam build() {
      return new SendInsiteParam(this);
    }
  }
}
