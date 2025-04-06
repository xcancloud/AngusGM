package cloud.xcan.angus.api.manager;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import java.util.Collection;
import java.util.List;

public interface GroupManager {

  List<Group> find(Collection<Long> ids);

  Group checkAndFind(Long id);

  List<Group> checkAndFind(Collection<Long> ids);

  void checkExists(Collection<Long> ids);

  Group checkValidAndFind(Long id);

  List<Group> checkValidAndFind(Collection<Long> ids);

  List<Group> findByTenantId(Long tenantId);

  List<Group> find(Long tenantId, Long directoryId);

  List<GroupUser> findGroupUser(Long tenantId, Long directoryId);

  List<GroupUser> findGroupUserByTenantId(Long tenantId);
}
