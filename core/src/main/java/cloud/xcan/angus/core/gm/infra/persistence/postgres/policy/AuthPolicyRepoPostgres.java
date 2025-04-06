package cloud.xcan.angus.core.gm.infra.persistence.postgres.policy;

import cloud.xcan.angus.core.gm.domain.policy.AuthPolicyRepo;
import org.springframework.stereotype.Repository;


@Repository("authPolicyRepo")
public interface AuthPolicyRepoPostgres extends AuthPolicyRepo {

}
