package cloud.xcan.angus.core.gm.application.cmd.dept;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;
import java.util.Set;

public interface DeptCmd {

  List<IdKey<Long, Object>> add(List<Dept> dept);

  void update(List<Dept> dept);

  List<IdKey<Long, Object>> replace(List<Dept> dept);

  void delete(Set<Long> ids);

}
