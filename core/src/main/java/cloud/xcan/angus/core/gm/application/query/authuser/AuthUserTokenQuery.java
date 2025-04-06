package cloud.xcan.angus.core.gm.application.query.authuser;

import cloud.xcan.angus.core.gm.domain.authuser.AuthUserToken;
import java.util.Collection;
import java.util.List;

public interface AuthUserTokenQuery {

  AuthUserToken value(Long id);

  List<AuthUserToken> list();

  List<AuthUserToken> checkAndFind(Collection<Long> ids);

  List<AuthUserToken> find0(Collection<Long> ids);

  void checkNameNotExisted(AuthUserToken userToken);

  void checkTokenQuota(Long userId, long inc);

  String encryptValue(String value);

  String decryptValue(String value);

}
