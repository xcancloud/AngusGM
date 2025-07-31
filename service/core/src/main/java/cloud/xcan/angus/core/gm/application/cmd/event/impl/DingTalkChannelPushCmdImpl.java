package cloud.xcan.angus.core.gm.application.cmd.event.impl;

import cloud.xcan.angus.core.gm.application.cmd.event.EventChannelPushCmdAbstract;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import cloud.xcan.angus.core.gm.infra.remote.push.ChannelSendResponse;
import cloud.xcan.angus.core.gm.infra.remote.push.DingTalkRobotRequest;
import java.net.URI;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of DingTalk channel push command for sending event notifications.
 * 
 * <p>This class provides DingTalk robot integration functionality including:</p>
 * <ul>
 *   <li>Sending markdown formatted messages to DingTalk robots</li>
 *   <li>Handling DingTalk API responses and error codes</li>
 *   <li>Converting event push data to DingTalk robot format</li>
 *   <li>Managing push response status and error messages</li>
 * </ul>
 * 
 * <p>The implementation converts event notifications to DingTalk markdown format
 * and sends them via DingTalk robot webhook.</p>
 */
@Slf4j
@Service("dingTalkPushChannelCmd")
public class DingTalkChannelPushCmdImpl extends EventChannelPushCmdAbstract {

  /**
   * Pushes event notification to DingTalk robot.
   * 
   * <p>This method performs DingTalk push including:</p>
   * <ul>
   *   <li>Converting event data to DingTalk robot request format</li>
   *   <li>Sending markdown message to DingTalk webhook</li>
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
      // Create DingTalk robot request with markdown content
      DingTalkRobotRequest dingTalkRobotRequest = new DingTalkRobotRequest();
      dingTalkRobotRequest.setMarkdown(new DingTalkRobotRequest.MarkDown(eventPush.getName(),
          eventPush.getContent()));
      
      // Send request to DingTalk robot webhook
      Map<?, ?> result = thirdPushClient
          .dingTalk(URI.create(eventPush.getAddress()), dingTalkRobotRequest);
      
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
    return ReceiveChannelType.DINGTALK;
  }
}
