package cloud.xcan.angus.core.gm.infra.persistence.postgres.sms;

import cloud.xcan.angus.core.gm.domain.sms.SmsTemplate;
import cloud.xcan.angus.core.gm.domain.sms.SmsTemplateRepo;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <p>SMS template repository PostgreSQL implementation</p>
 */
@Profile("postgres")
@Repository
public interface SmsTemplateRepoPostgres extends SmsTemplateRepo, JpaRepository<SmsTemplate, Long> {
}

