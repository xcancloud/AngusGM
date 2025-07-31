package cloud.xcan.angus.core.gm.application.cmd.event.impl;

import cloud.xcan.angus.core.gm.application.cmd.event.EventChannelPushCmdAbstract;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import cloud.xcan.angus.core.gm.infra.remote.push.ChannelSendResponse;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of webhook channel push command for sending event notifications.
 * 
 * <p>This class provides webhook integration functionality including:</p>
 * <ul>
 *   <li>Sending event notifications to webhook endpoints</li>
 *   <li>Handling webhook HTTP requests and responses</li>
 *   <li>Managing webhook push response status</li>
 *   <li>Converting event data to webhook format</li>
 * </ul>
 * 
 * <p>The implementation sends event notifications to configured webhook URLs
 * and handles response status appropriately.</p>
 */
@Slf4j
@Service("webhookPushChannelCmd")
public class WebhookChannelPushCmdImpl extends EventChannelPushCmdAbstract {

  /**
   * Pushes event notification to webhook endpoint.
   * 
   * <p>This method performs webhook push including:</p>
   * <ul>
   *   <li>Converting event data to webhook format</li>
   *   <li>Sending HTTP request to webhook URL</li>
   *   <li>Handling webhook response status</li>
   *   <li>Returning appropriate channel response</li>
   * </ul>
   * 
   * @param eventPush Event push data containing notification details
   * @return Channel send response with success status and message
   */
  @Override
  public ChannelSendResponse push(EventPush eventPush) {
    try {
      // Send event data to webhook endpoint
      thirdPushClient.webhook(URI.create(eventPush.getAddress()), eventPush);
      return new ChannelSendResponse();
    } catch (Exception e) {
      // Return failure response with error message
      return new ChannelSendResponse(false, e.getMessage());
    }
  }

  @Override
  public ReceiveChannelType getPkey() {
    return ReceiveChannelType.WEBHOOK;
  }
}
