package cloud.xcan.angus.core.gm.application.cmd.email;

import cloud.xcan.angus.core.gm.domain.email.EmailTemplate;

/**
 * Email template command service interface
 */
public interface EmailTemplateCmd {

  /**
   * Create email template
   */
  EmailTemplate create(EmailTemplate template);

  /**
   * Update email template
   */
  EmailTemplate update(EmailTemplate template);

  /**
   * Update template status
   */
  EmailTemplate updateStatus(Long id, String status);

  /**
   * Delete email template
   */
  void delete(Long id);
}

