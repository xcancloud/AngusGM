package cloud.xcan.angus.core.gm.infra.persistence.postgres.sms;

import cloud.xcan.angus.core.gm.domain.sms.SmsProvider;
import cloud.xcan.angus.core.gm.domain.sms.SmsProviderRepo;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <p>SMS provider repository PostgreSQL implementation</p>
 */
@Profile("postgres")
@Repository
public interface SmsProviderRepoPostgres extends SmsProviderRepo, JpaRepository<SmsProvider, Long> {
}

