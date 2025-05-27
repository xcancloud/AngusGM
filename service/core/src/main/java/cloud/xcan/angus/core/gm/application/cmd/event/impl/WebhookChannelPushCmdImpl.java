package cloud.xcan.angus.core.gm.application.cmd.event.impl;

import cloud.xcan.angus.core.gm.application.cmd.event.EventChannelPushCmdAbstract;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import cloud.xcan.angus.core.gm.infra.remote.push.ChannelSendResponse;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service("webhookPushChannelCmd")
public class WebhookChannelPushCmdImpl extends EventChannelPushCmdAbstract {

  @Override
  public ChannelSendResponse push(EventPush eventPush) {
    try {
      thirdPushClient.webhook(URI.create(eventPush.getAddress()), eventPush);
      return new ChannelSendResponse();
    } catch (Exception e) {
      return new ChannelSendResponse(false, e.getMessage());
    }
  }

  @Override
  public ReceiveChannelType getPkey() {
    return ReceiveChannelType.WEBHOOK;
  }
}
