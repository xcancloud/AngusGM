package cloud.xcan.angus.core.gm.application.cmd.email;

import cloud.xcan.angus.core.gm.domain.email.EmailSmtp;

/**
 * Email SMTP configuration command service interface
 */
public interface EmailSmtpCmd {

  /**
   * Create or update SMTP configuration
   */
  EmailSmtp save(EmailSmtp smtp);
}

