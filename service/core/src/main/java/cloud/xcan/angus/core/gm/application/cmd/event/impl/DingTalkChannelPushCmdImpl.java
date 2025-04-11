package cloud.xcan.angus.core.gm.application.cmd.event.impl;

import cloud.xcan.angus.core.gm.application.cmd.event.EventChannelPushCmdAbstract;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import cloud.xcan.angus.core.gm.infra.remote.ChannelSendResponse;
import cloud.xcan.angus.core.gm.infra.remote.DingTalkRobotRequest;
import java.net.URI;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service("dingTalkPushChannelCmd")
public class DingTalkChannelPushCmdImpl extends EventChannelPushCmdAbstract {

  @Override
  public ChannelSendResponse push(EventPush eventPush) {
    try {
      DingTalkRobotRequest dingTalkRobotRequest = new DingTalkRobotRequest();
      dingTalkRobotRequest.setMarkdown(new DingTalkRobotRequest.MarkDown(eventPush.getName(),
          eventPush.getContent()));
      Map<?, ?> result = thirdPushClient
          .dingTalk(URI.create(eventPush.getAddress()), dingTalkRobotRequest);
      return new ChannelSendResponse(Integer.parseInt(result.get("errcode").toString()) == 0,
          result.get("errmsg").toString());
    } catch (Exception e) {
      return new ChannelSendResponse(false, e.getMessage());
    }
  }

  @Override
  public ReceiveChannelType getPkey() {
    return ReceiveChannelType.DINGTALK;
  }
}
