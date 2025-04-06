package cloud.xcan.angus.core.gm.interfaces.authuser.facade.vo.sign;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class SignVo {

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("issued_at")
  private Instant issuedAt;

  @JsonProperty("expires_at")
  private Instant expiresAt;

  @JsonProperty("token_type")
  private String tokenType;

  @JsonProperty("refresh_token")
  private String refreshToken;

  private Set<String> scopes;

}
