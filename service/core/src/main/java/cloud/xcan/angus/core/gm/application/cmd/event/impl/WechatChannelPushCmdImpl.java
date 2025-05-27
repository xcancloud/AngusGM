package cloud.xcan.angus.core.gm.application.cmd.event.impl;

import cloud.xcan.angus.core.gm.application.cmd.event.EventChannelPushCmdAbstract;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import cloud.xcan.angus.core.gm.infra.remote.push.ChannelSendResponse;
import cloud.xcan.angus.core.gm.infra.remote.push.WeChatRobotRequest;
import java.net.URI;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service("wechatPushChannelCmd")
public class WechatChannelPushCmdImpl extends EventChannelPushCmdAbstract {

  @Override
  public ChannelSendResponse push(EventPush eventPush) {
    try {
      WeChatRobotRequest weChatRobotRequest = new WeChatRobotRequest();
      weChatRobotRequest.setMarkdown(new WeChatRobotRequest.MarkDown(eventPush.getContent()));
      Map<?, ?> result = thirdPushClient.wechat(URI.create(eventPush.getAddress()),
          weChatRobotRequest);
      return new ChannelSendResponse(Integer.parseInt(result.get("errcode").toString()) == 0,
          result.get("errmsg").toString());
    } catch (Exception e) {
      return new ChannelSendResponse(false, e.getMessage());
    }
  }

  @Override
  public ReceiveChannelType getPkey() {
    return ReceiveChannelType.WECHAT;
  }
}
