package cloud.xcan.angus.api.gm.client.vo;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ClientSignupVo {

  private String clientId;

  private String clientSecret;

}
