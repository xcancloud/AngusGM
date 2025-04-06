package cloud.xcan.angus.api.manager;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import java.util.Collection;
import java.util.List;

public interface DeptManager {

  List<Dept> find(Collection<Long> ids);

  Dept checkAndFind(Long id);

  List<Dept> checkAndFind(Collection<Long> ids);

  void checkExists(Collection<Long> ids);

  List<Dept> checkAndGetParent(Long tenantId, List<Dept> depts);
}
