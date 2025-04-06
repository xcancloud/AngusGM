package cloud.xcan.angus.core.gm.application.query.user;

import cloud.xcan.angus.core.gm.domain.user.directory.UserDirectory;
import java.util.Collection;
import java.util.List;


public interface UserDirectoryQuery {

  UserDirectory detail(Long id);

  List<UserDirectory> list();

  UserDirectory find(Long id);

  UserDirectory checkAndFind(Long id);

  List<UserDirectory> checkAndFind(Collection<Long> ids);

  void checkNameExisted(UserDirectory directory);

  void checkQuota(int incr);

}
