package cloud.xcan.angus.core.gm.application.query.policy;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface AuthPolicyGroupQuery {

  Page<Group> policyGroupList(GenericSpecification<Group> spec, PageRequest pageable);

  Page<Group> policyUnauthGroupList(GenericSpecification<Group> spec, PageRequest pageable);

  Page<AuthPolicy> groupPolicyList(GenericSpecification<AuthPolicy> spec, PageRequest pageable);

  Page<AuthPolicy> groupUnauthPolicyList(GenericSpecification<AuthPolicy> spec,
      PageRequest pageable);

}
