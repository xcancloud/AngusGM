package cloud.xcan.angus.core.gm.interfaces.auth.facade.vo.sign;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignVo {

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("expires_in")
  private Integer expiresIn;

  @JsonProperty("token_type")
  private String tokenType;

  @JsonProperty("refresh_token")
  private String refreshToken;

  private String scope;

}
