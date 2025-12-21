package cloud.xcan.angus.core.gm.application.cmd.authentication;


import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.enums.SignInType;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.Map;


public interface AuthUserSignCmd {

  IdKey<Long, Object> signup(AuthUser user);

  Map<String, String> signin(String clientId, String clientSecret, SignInType signinType,
      Long userId, String account, String password, String scope, String deviceId);

  Map<String, String> renew(String clientId, String clientSecret, String refreshToken);

  void signout(String clientId, String clientSecret, String accessToken);

  void forgetPassword(Long userId, String newPassword, String linkSecret);

}
