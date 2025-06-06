package cloud.xcan.angus.core.gm.application.cmd.event;

import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import cloud.xcan.angus.core.gm.infra.remote.push.ChannelSendResponse;
import cloud.xcan.angus.core.gm.infra.remote.push.ThirdPushClient;
import feign.Feign;
import feign.Target;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class EventChannelPushCmdAbstract implements EventChannelPushCmd {

  public ThirdPushClient thirdPushClient;

  @Override
  public abstract ChannelSendResponse push(EventPush eventPush);

  @Override
  public abstract ReceiveChannelType getPkey();

  @Autowired
  public void callerService(Decoder decoder, Encoder encoder) {
    thirdPushClient = Feign.builder().encoder(encoder)
        .decoder(decoder).target(Target.EmptyTarget.create(ThirdPushClient.class));
  }
}
