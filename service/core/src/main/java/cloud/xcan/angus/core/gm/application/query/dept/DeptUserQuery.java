package cloud.xcan.angus.core.gm.application.query.dept;

import cloud.xcan.angus.api.commonlink.user.dept.DeptUser;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeptUserQuery {

  Page<DeptUser> findUserDept(GenericSpecification<DeptUser> spec, Pageable pageable);

  Page<DeptUser> findDeptUser(GenericSpecification<DeptUser> spec, Pageable pageable);

  void setHasSubDept(List<DeptUser> deptUsersDb);

  List<DeptUser> findAllByUserId(Long userId);

  void checkDeptUserAppendQuota(Long tenantId, long incr, Long deptId);

  void checkUserDeptReplaceQuota(Long tenantId, long incr, Long userId);

  void checkUserDeptAppendQuota(Long tenantId, long incr, Long userId);
}
