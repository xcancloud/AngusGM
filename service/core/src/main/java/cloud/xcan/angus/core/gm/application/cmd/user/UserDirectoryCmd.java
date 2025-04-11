package cloud.xcan.angus.core.gm.application.cmd.user;


import cloud.xcan.angus.core.gm.domain.user.directory.UserDirectory;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectorySyncResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;
import java.util.Map;


public interface UserDirectoryCmd {

  IdKey<Long, Object> add(UserDirectory directory, boolean onlyTest);

  void replace(UserDirectory directory);

  void enabled(Long id, boolean enabled);

  void reorder(Long id, Integer sequence);

  DirectorySyncResult sync(Long id, boolean onlyTest);

  Map<String, DirectorySyncResult> sync();

  void delete(Long id, boolean deleteSync);

  DirectorySyncResult test(UserDirectory directory);

}
