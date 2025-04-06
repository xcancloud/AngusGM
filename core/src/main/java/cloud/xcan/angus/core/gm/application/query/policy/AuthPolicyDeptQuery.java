package cloud.xcan.angus.core.gm.application.query.policy;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface AuthPolicyDeptQuery {

  Page<Dept> policyDeptList(GenericSpecification<Dept> spec, PageRequest pageable);

  Page<Dept> policyUnauthDeptList(GenericSpecification<Dept> spec, PageRequest pageable);

  Page<AuthPolicy> deptPolicyList(GenericSpecification<AuthPolicy> spec, PageRequest pageable);

  Page<AuthPolicy> deptUnauthPolicyList(GenericSpecification<AuthPolicy> spec,
      PageRequest pageable);

}
