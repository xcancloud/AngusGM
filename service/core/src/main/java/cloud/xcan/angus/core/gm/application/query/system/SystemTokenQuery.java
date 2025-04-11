package cloud.xcan.angus.core.gm.application.query.system;

import cloud.xcan.angus.core.gm.domain.system.SystemToken;
import java.util.List;


public interface SystemTokenQuery {

  SystemToken auth(Long id);

  SystemToken value(Long id);

  List<SystemToken> list();

  SystemToken find0(Long id);

  void checkNameNotExisted(SystemToken systemToken);

  void checkTokenQuota(Long tenantId, long incr);

  String encryptValue(String value);

  String decryptValue(String value);

}
