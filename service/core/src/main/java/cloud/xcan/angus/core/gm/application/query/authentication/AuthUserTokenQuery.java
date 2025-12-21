package cloud.xcan.angus.core.gm.application.query.authentication;

import cloud.xcan.angus.core.gm.domain.authentication.AuthUserToken;
import java.util.Collection;
import java.util.List;

public interface AuthUserTokenQuery {

  AuthUserToken value(Long id);

  List<AuthUserToken> list(String appCode);

  List<AuthUserToken> checkAndFind(Collection<Long> ids);

  List<AuthUserToken> find0(Collection<Long> ids);

  void checkNameNotExisted(AuthUserToken userToken);

  void checkTokenQuota(Long userId, long inc);

  String encryptValue(String value);

  String decryptValue(String value);

}
