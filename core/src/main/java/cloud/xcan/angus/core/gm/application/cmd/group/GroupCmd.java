package cloud.xcan.angus.core.gm.application.cmd.group;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;
import java.util.Set;

public interface GroupCmd {

  List<IdKey<Long, Object>> add(List<Group> groups);

  void update(List<Group> groups);

  List<IdKey<Long, Object>> replace(List<Group> groups);

  void delete(Set<Long> ids);

  void delete0(Set<Long> ids);

  void enabled(List<Group> groups);

  void emptyDirectoryGroups(Set<Long> groupIds);

  void emptyDirectoryGroups(Long directoryId);

  void deleteByDirectory(Long id, boolean deleteSync);

}
