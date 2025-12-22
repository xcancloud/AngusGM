package cloud.xcan.angus.core.gm.infra.persistence.postgres.sms;

import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.domain.sms.SmsRepo;
import org.springframework.stereotype.Repository;

/**
 * <p>SMS repository PostgreSQL implementation</p>
 */
@Repository
public interface SmsRepoPostgres extends SmsRepo {
    // Spring Data JPA will implement methods automatically
}
