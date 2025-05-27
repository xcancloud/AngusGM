package cloud.xcan.angus.core.gm.infra.remote.push;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChannelSendResponse {

  private boolean success;
  private String message;

  public ChannelSendResponse() {
    this(true, "SUCCESS");
  }

  public ChannelSendResponse(boolean success, String message) {
    this.success = success;
    this.message = message;
  }

}
