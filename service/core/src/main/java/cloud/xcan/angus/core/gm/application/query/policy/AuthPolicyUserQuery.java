package cloud.xcan.angus.core.gm.application.query.policy;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface AuthPolicyUserQuery {

  Page<User> policyUserList(GenericSpecification<User> spec, PageRequest pageable);

  Page<User> policyUnauthUserList(GenericSpecification<User> spec, PageRequest pageable);

  Page<AuthPolicy> userPolicyList(GenericSpecification<AuthPolicy> spec,
      PageRequest pageable);

  Page<AuthPolicy> userAssociatedPolicyList(GenericSpecification<AuthPolicy> spec,
      PageRequest pageable, boolean currentAdmin);

  Page<AuthPolicy> userUnauthPolicyList(GenericSpecification<AuthPolicy> spec,
      PageRequest pageable);

  List<App> userAppList(Long userId);

  App userAppFuncList(Long userId, String appIdOrCode, Boolean joinApi, Boolean joinTag
      , Boolean onlyEnabled);

  void checkSwitchUnAuthOrgCondition(GenericSpecification<AuthPolicy> spec);

}
