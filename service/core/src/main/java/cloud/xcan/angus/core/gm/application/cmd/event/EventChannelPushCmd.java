package cloud.xcan.angus.core.gm.application.cmd.event;

import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import cloud.xcan.angus.core.gm.infra.remote.push.ChannelSendResponse;


public interface EventChannelPushCmd {

  ChannelSendResponse push(EventPush eventPush);

  ReceiveChannelType getPkey();

}
