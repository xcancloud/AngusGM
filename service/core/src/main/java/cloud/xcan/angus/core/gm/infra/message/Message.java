package cloud.xcan.angus.core.gm.infra.message;

import cloud.xcan.angus.api.enums.MessageType;
import cloud.xcan.angus.api.enums.PushMediaType;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class Message {

  //  public final static String MESSAGE_CENTER_NOTICE = "MESSAGE_CENTER_NOTICE";
  //  public final static String MESSAGE_CENTER_SIGN_OUT = "MESSAGE_CENTER_SIGN_OUT";

  public String id;

  public MessageType type;

  public PushMediaType mediaType;

  public ReceiveObjectType receiveObjectType;

  private List<Long> receiveObjectIds;

  private String title;

  private String content;

  /**
   * principal
   */
  private String from;

  private Long sendBy;

  private String sendByName;

  private Date sendDate;

  public Message() {
  }

  private Message(Builder builder) {
    setId(builder.id);
    setType(builder.type);
    setMediaType(builder.mediaType);
    setReceiveObjectType(builder.receiveObjectType);
    setReceiveObjectIds(builder.receiveObjectIds);
    setTitle(builder.title);
    setContent(builder.content);
    setFrom(builder.from);
    setSendBy(builder.sendBy);
    setSendByName(builder.sendByName);
    setSendDate(builder.sendDate);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private String id;
    private MessageType type;
    private PushMediaType mediaType;
    private ReceiveObjectType receiveObjectType;
    private List<Long> receiveObjectIds;
    private String title;
    private String content;
    private String from;
    private Long sendBy;
    private String sendByName;
    private Date sendDate;

    private Builder() {
    }

    public Builder id(String val) {
      id = val;
      return this;
    }

    public Builder type(MessageType val) {
      type = val;
      return this;
    }

    public Builder mediaType(PushMediaType val) {
      mediaType = val;
      return this;
    }

    public Builder receiveObjectType(ReceiveObjectType val) {
      receiveObjectType = val;
      return this;
    }

    public Builder receiveObjectIds(List<Long> val) {
      receiveObjectIds = val;
      return this;
    }

    public Builder title(String val) {
      title = val;
      return this;
    }

    public Builder content(String val) {
      content = val;
      return this;
    }

    public Builder from(String val) {
      from = val;
      return this;
    }

    public Builder sendBy(Long val) {
      sendBy = val;
      return this;
    }

    public Builder sendByName(String val) {
      sendByName = val;
      return this;
    }

    public Builder sendDate(Date val) {
      sendDate = val;
      return this;
    }

    public Message build() {
      return new Message(this);
    }
  }
}
