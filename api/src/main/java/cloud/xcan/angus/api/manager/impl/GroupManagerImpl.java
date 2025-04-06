package cloud.xcan.angus.api.manager.impl;

import static cloud.xcan.angus.api.manager.UCManagerMessage.GROUP_IS_DISABLED_CODE;
import static cloud.xcan.angus.api.manager.UCManagerMessage.GROUP_IS_DISABLED_T;
import static cloud.xcan.angus.core.biz.BizAssert.assertTrue;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.group.GroupRepo;
import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.api.commonlink.user.group.GroupUserRepo;
import cloud.xcan.angus.api.manager.GroupManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@Biz
public class GroupManagerImpl implements GroupManager {

  @Autowired(required = false)
  private GroupRepo groupRepo;

  @Autowired(required = false)
  private GroupUserRepo groupUserRepo;

  @Override
  public List<Group> find(Collection<Long> ids) {
    return groupRepo.findAllById(ids);
  }

  @Override
  public Group checkAndFind(Long id) {
    return groupRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Group"));
  }

  @Override
  public List<Group> checkAndFind(Collection<Long> ids) {
    List<Group> groups = groupRepo.findAllById(ids);
    assertResourceNotFound(isNotEmpty(groups), ids.iterator().next(), "Group");

    if (ids.size() != groups.size()) {
      for (Group group : groups) {
        assertResourceNotFound(ids.contains(group.getId()), group.getId(), "Group");
      }
    }
    return groups;
  }

  @Override
  public void checkExists(Collection<Long> ids) {
    List<Long> groupIdsDb = groupRepo.findIdsByIdIn(ids);
    assertResourceNotFound(isNotEmpty(groupIdsDb), ids.iterator().next(), "Group");

    if (ids.size() != groupIdsDb.size()) {
      for (Long groupId : groupIdsDb) {
        assertResourceNotFound(ids.contains(groupId), groupId, "Group");
      }
    }
  }

  @Override
  public Group checkValidAndFind(Long id) {
    Group groupDb = groupRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Group"));
    assertTrue(groupDb.getEnabled(), GROUP_IS_DISABLED_CODE, GROUP_IS_DISABLED_T,
        new Object[]{groupDb.getName()});
    return groupDb;
  }

  @Override
  public List<Group> checkValidAndFind(Collection<Long> ids) {
    List<Group> groups = groupRepo.findAllById(ids);
    assertResourceNotFound(isNotEmpty(groups), ids.iterator().next(), "Group");

    if (ids.size() != groups.size()) {
      for (Group group : groups) {
        assertResourceNotFound(ids.contains(group.getId()), group.getId(), "Group");
      }
    }

    for (Group group : groups) {
      assertTrue(group.getEnabled(), GROUP_IS_DISABLED_CODE, GROUP_IS_DISABLED_T
          , new Object[]{group.getName()});
    }
    return groups;
  }

  @Override
  public List<Group> findByTenantId(Long tenantId) {
    return groupRepo.findAllByTenantId(tenantId);
  }

  @Override
  public List<Group> find(Long tenantId, Long directoryId) {
    return groupRepo.findByTenantIdAndDirectoryId(tenantId, directoryId);
  }

  @Override
  public List<GroupUser> findGroupUser(Long tenantId, Long directoryId) {
    return groupUserRepo.findByTenantIdAndDirectoryId(tenantId, directoryId);
  }

  @Override
  public List<GroupUser> findGroupUserByTenantId(Long tenantId) {
    return groupUserRepo.findByTenantId(tenantId);
  }
}
