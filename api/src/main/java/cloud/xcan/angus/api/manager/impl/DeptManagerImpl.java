package cloud.xcan.angus.api.manager.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_ROOT_PID;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.commonlink.dept.DeptRepo;
import cloud.xcan.angus.api.manager.DeptManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

@Biz
public class DeptManagerImpl implements DeptManager {

  @Autowired(required = false)
  private DeptRepo deptRepo;

  @Override
  public List<Dept> find(Collection<Long> ids) {
    return deptRepo.findAllById(ids);
  }

  @Override
  public Dept checkAndFind(Long id) {
    return deptRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Department"));
  }

  @Override
  public List<Dept> checkAndFind(Collection<Long> ids) {
    List<Dept> depts = deptRepo.findAllById(ids);
    assertResourceNotFound(isNotEmpty(depts), ids.iterator().next(), "Department");
    if (ids.size() != depts.size()) {
      for (Dept dept : depts) {
        assertResourceNotFound(ids.contains(dept.getId()), dept.getId(), "Department");
      }
    }
    return depts;
  }

  @Override
  public void checkExists(Collection<Long> ids) {
    List<Long> deptIdsDb = deptRepo.findIdsByIdIn(ids);
    assertResourceNotFound(isNotEmpty(deptIdsDb), ids.iterator().next(), "Department");
    if (ids.size() != deptIdsDb.size()) {
      for (Long deptId : deptIdsDb) {
        assertResourceNotFound(ids.contains(deptId), deptId, "Department");
      }
    }
  }

  @Override
  public List<Dept> checkAndGetParent(Long tenantId, List<Dept> depts) {
    List<Dept> parentsDb = deptRepo.findByTenantIdAndIdIn(tenantId,
        depts.stream().filter(Dept::hasParent).map(Dept::getPid)
            .collect(Collectors.toList()));
    if (isEmpty(parentsDb)) {
      return null;
    }
    Set<Long> parentIds = parentsDb.stream().map(Dept::getId).collect(Collectors.toSet());
    for (Dept dept : depts) {
      assertResourceNotFound(dept.getPid().equals(DEFAULT_ROOT_PID)
          || parentIds.contains(dept.getPid()), dept.getPid(), "Department");
    }
    return parentsDb;
  }

}
