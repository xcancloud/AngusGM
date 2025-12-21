package cloud.xcan.angus.core.gm.application.query.authentication;

import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.enums.SignInType;
import cloud.xcan.angus.security.model.CustomOAuth2User;
import java.util.List;


public interface AuthUserQuery {

  List<AuthUser> findByAccountAndPassword(String account, String password);

  void checkPassword(Long id, String password);

  AuthUser checkAndFindByAccount(Long userId, SignInType signinType, String account,
      String password);

  void checkLinkSecret(Long userId, String linkSecret, EmailBizKey bizKey);

  void checkSmsLinkSecret(Long userId, String linkSecret, SmsBizKey bizKey);

  void checkUserValid(AuthUser user);

  void checkOperationPlatformLogin(CustomOAuth2User user);

  List<AuthUser> checkAndFind(List<Long> ids);

  AuthUser checkAndFind(Long id);

  AuthUser findByUsername(String username);

  List<AuthUser> findByEmail(String email);

  List<AuthUser> findByMobile(String mobile);

}
