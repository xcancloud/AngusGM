package cloud.xcan.angus.core.gm.application.cmd.authuser;


import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.enums.SignInType;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.Set;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;


public interface AuthUserSignCmd {

  IdKey<Long, Object> signup(AuthUser user);

  OAuth2AccessTokenAuthenticationToken signin(String clientId, String clientSecret,
      Set<String> scopes, SignInType signinType, Long userId, String account, String password,
      String deviceId);

  OAuth2AccessTokenAuthenticationToken renew(String clientId, String clientSecret,
      String refreshToken, Set<String> scopes);

  void signout(String clientId, String clientSecret, String accessToken);

  void forgetPassword(Long userId, String newPassword, String linkSecret);

}
