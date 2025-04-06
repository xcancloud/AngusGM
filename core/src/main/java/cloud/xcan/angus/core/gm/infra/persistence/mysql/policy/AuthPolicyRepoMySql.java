package cloud.xcan.angus.core.gm.infra.persistence.mysql.policy;

import cloud.xcan.angus.core.gm.domain.policy.AuthPolicyRepo;
import org.springframework.stereotype.Repository;


@Repository("authPolicyRepo")
public interface AuthPolicyRepoMySql extends AuthPolicyRepo {

}
