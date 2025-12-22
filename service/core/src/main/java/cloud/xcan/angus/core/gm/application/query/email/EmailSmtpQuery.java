package cloud.xcan.angus.core.gm.application.query.email;

import cloud.xcan.angus.core.gm.domain.email.EmailSmtp;
import java.util.Optional;

/**
 * Email SMTP configuration query service interface
 */
public interface EmailSmtpQuery {

  /**
   * Find default SMTP configuration
   */
  Optional<EmailSmtp> findDefault();
}

