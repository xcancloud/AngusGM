package cloud.xcan.angus.core.gm.infra.persistence.mysql.sms;

import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.domain.sms.SmsRepo;
import org.springframework.stereotype.Repository;

/**
 * <p>SMS repository MySQL implementation</p>
 */
@Repository
public interface SmsRepoMysql extends SmsRepo {
    // Spring Data JPA will implement methods automatically
}
