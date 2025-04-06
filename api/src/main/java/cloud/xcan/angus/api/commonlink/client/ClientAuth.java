package cloud.xcan.angus.api.commonlink.client;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ClientAuth {

  private String clientId;
  private String clientSecret;

}
