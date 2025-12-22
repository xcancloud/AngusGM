package cloud.xcan.angus.core.gm.infra.persistence.mysql.sms;

import cloud.xcan.angus.core.gm.domain.sms.SmsTemplate;
import cloud.xcan.angus.core.gm.domain.sms.SmsTemplateRepo;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <p>SMS template repository MySQL implementation</p>
 */
@Profile("mysql")
@Repository
public interface SmsTemplateRepoMysql extends SmsTemplateRepo, JpaRepository<SmsTemplate, Long> {
}

