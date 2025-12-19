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

/**
 * Implementation of WeChat channel push command for sending event notifications.
 *
 * <p>This class provides WeChat robot integration functionality including:</p>
 * <ul>
 *   <li>Sending markdown formatted messages to WeChat robots</li>
 *   <li>Handling WeChat API responses and error codes</li>
 *   <li>Converting event push data to WeChat robot format</li>
 *   <li>Managing push response status and error messages</li>
 * </ul>
 *
 * <p>The implementation converts event notifications to WeChat markdown format
 * and sends them via WeChat robot webhook.</p>
 */
@Slf4j
@Service("wechatPushChannelCmd")
public class WechatChannelPushCmdImpl extends EventChannelPushCmdAbstract {

  /**
   * Pushes event notification to WeChat robot.
   *
   * <p>This method performs WeChat push including:</p>
   * <ul>
   *   <li>Converting event data to WeChat robot request format</li>
   *   <li>Sending markdown message to WeChat webhook</li>
   *   <li>Handling API response and error codes</li>
   *   <li>Returning appropriate channel response</li>
   * </ul>
   *
   * @param eventPush Event push data containing notification details
   * @return Channel send response with success status and message
   */
  @Override
  public ChannelSendResponse push(EventPush eventPush) {
    try {
      // Create WeChat robot request with markdown content
      WeChatRobotRequest weChatRobotRequest = new WeChatRobotRequest();
      weChatRobotRequest.setMarkdown(new WeChatRobotRequest.MarkDown(eventPush.getContent()));

      // Send request to WeChat robot webhook
      Map<?, ?> result = thirdPushClient.wechat(URI.create(eventPush.getAddress()),
          weChatRobotRequest);

      // Parse response and return appropriate status
      return new ChannelSendResponse(Integer.parseInt(result.get("errcode").toString()) == 0,
          result.get("errmsg").toString());
    } catch (Exception e) {
      // Return failure response with error message
      return new ChannelSendResponse(false, e.getMessage());
    }
  }

  @Override
  public ReceiveChannelType getPkey() {
    return ReceiveChannelType.WECHAT;
  }
}
