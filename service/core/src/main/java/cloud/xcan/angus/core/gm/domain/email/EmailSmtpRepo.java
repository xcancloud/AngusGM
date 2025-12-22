package cloud.xcan.angus.core.gm.domain.email;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Email SMTP configuration repository interface
 */
@NoRepositoryBean
public interface EmailSmtpRepo extends BaseRepository<EmailSmtp, Long> {

  /**
   * Find default SMTP configuration
   */
  EmailSmtp findByIsDefaultTrue();
}

