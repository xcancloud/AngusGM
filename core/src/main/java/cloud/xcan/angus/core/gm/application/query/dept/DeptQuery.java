package cloud.xcan.angus.core.gm.application.query.dept;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.core.gm.domain.dept.DeptSubCount;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeptQuery {

  Dept navigation(Long id);

  Dept detail(Long id);

  Page<Dept> list(GenericSpecification<Dept> spec, Pageable pageable);

  DeptSubCount subCount(Long id);

  void setHasSubDept(List<Dept> deptsDb);

  Dept checkAndFind(Long id);

  List<Dept> checkAndFind(Collection<Long> deptIds);

  List<Dept> checkAndGetParent(Long tenantId, List<Dept> depts);

  void checkNestedDuplicates(List<Dept> deptsDb);

  void checkAddDeptCode(Long tenantId, List<Dept> depts);

  void checkUpdateDeptCode(Long tenantId, List<Dept> depts);

  void checkDeptQuota(Long tenantId, long incr);

  void checkDeptLevelQuota(Long tenantId, List<Dept> depts, Map<Long, Dept> deptsDbMap,
      Map<Long, Dept> parentDeptsDbMap, boolean add);

  void checkTagQuota(Long optTenantId, List<Dept> depts);
}
