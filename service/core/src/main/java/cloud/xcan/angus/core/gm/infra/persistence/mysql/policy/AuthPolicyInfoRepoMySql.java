package cloud.xcan.angus.core.gm.infra.persistence.mysql.policy;

import cloud.xcan.angus.api.commonlink.policy.AuthPolicyInfoRepo;
import org.springframework.stereotype.Repository;


@Repository("commonAuthPolicyInfoRepo")
public interface AuthPolicyInfoRepoMySql extends AuthPolicyInfoRepo {

}
