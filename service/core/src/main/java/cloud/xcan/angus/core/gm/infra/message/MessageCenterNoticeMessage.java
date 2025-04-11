package cloud.xcan.angus.core.gm.infra.message;

import cloud.xcan.angus.api.enums.PushMediaType;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageCenterNoticeMessage extends WMessage<MessageCenterNotice> {

  public final static String MESSAGE_CENTER_NOTICE = "MESSAGE_CENTER_NOTICE";
  public final static String MESSAGE_CENTER_SIGN_OUT = "MESSAGE_CENTER_SIGN_OUT";

  private MessageCenterNotice content;

  public MessageCenterNoticeMessage(String id, String bizKey, ReceiveObjectType receiveObjectType,
      PushMediaType pushMediaType, MessageCenterNotice content) {
    super(id, bizKey, receiveObjectType, pushMediaType);
    this.content = content;
  }

  private MessageCenterNoticeMessage(Builder builder) {
    this(builder.id, builder.bizKey, builder.receiveObjectType, builder.pushMediaType,
        builder.content);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @Override
  public MessageCenterNotice getContent() {
    return this.content;
  }

  public static final class Builder {

    private String id;
    private String bizKey;
    private ReceiveObjectType receiveObjectType;
    private PushMediaType pushMediaType;
    private MessageCenterNotice content;

    private Builder() {
    }

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder bizKey(String bizKey) {
      this.bizKey = bizKey;
      return this;
    }

    public Builder receiveObjectType(ReceiveObjectType receiveObjectType) {
      this.receiveObjectType = receiveObjectType;
      return this;
    }

    public Builder pushMediaType(PushMediaType pushMediaType) {
      this.pushMediaType = pushMediaType;
      return this;
    }

    public Builder content(MessageCenterNotice content) {
      this.content = content;
      return this;
    }

    public MessageCenterNoticeMessage build() {
      return new MessageCenterNoticeMessage(this);
    }
  }
}
