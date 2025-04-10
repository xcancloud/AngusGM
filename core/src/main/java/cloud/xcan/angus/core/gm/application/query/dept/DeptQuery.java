package cloud.xcan.angus.core.gm.application.query.dept;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.core.gm.domain.dept.DeptSubCount;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.HashSet;
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

  List<Dept> findByIdIn(Collection<Long> ids);

  Dept checkAndFind(Long id);

  List<Dept> checkAndFind(Collection<Long> deptIds);

  List<Dept> checkAndGetParent(Long tenantId, List<Dept> dept);

  void checkNestedDuplicates(List<Dept> deptDb);

  void checkAddDeptCode(Long tenantId, List<Dept> dept);

  void checkUpdateDeptCode(Long tenantId, List<Dept> dept);

  void checkDeptQuota(Long tenantId, long incr);

  void checkDeptLevelQuota(Long tenantId, List<Dept> dept, Map<Long, Dept> deptDbMap,
      Map<Long, Dept> parentDeptDbMap, boolean add);

  void checkTagQuota(Long optTenantId, List<Dept> dept);


}
