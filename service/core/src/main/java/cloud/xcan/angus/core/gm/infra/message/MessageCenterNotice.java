package cloud.xcan.angus.core.gm.infra.message;

import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class MessageCenterNotice extends ValueObjectSupport<MessageCenterNotice> {

  private String title;

  private String content;

  private Long sendUserId;

  private String sendUserName;

  private Date sendDate;

  private List<Long> receiveObjectIds;

  @Override
  public MessageCenterNotice copy() {
    return new MessageCenterNotice()
        .setTitle(this.title).setContent(this.content)
        .setSendUserId(sendUserId).setSendDate(this.sendDate)
        .setReceiveObjectIds(receiveObjectIds);
  }

}
