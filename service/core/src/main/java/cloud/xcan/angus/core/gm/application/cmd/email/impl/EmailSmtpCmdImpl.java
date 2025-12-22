package cloud.xcan.angus.core.gm.application.cmd.email.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailSmtpCmd;
import cloud.xcan.angus.core.gm.domain.email.EmailSmtp;
import cloud.xcan.angus.core.gm.domain.email.EmailSmtpRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of email SMTP configuration command service
 */
@Biz
public class EmailSmtpCmdImpl extends CommCmd<EmailSmtp, Long> implements EmailSmtpCmd {

  @Resource
  private EmailSmtpRepo emailSmtpRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public EmailSmtp save(EmailSmtp smtp) {
    return new BizTemplate<EmailSmtp>() {
      @Override
      protected void checkParams() {
        // If setting as default, unset other defaults
        if (Boolean.TRUE.equals(smtp.getIsDefault())) {
          EmailSmtp existingDefault = emailSmtpRepo.findByIsDefaultTrue();
          if (existingDefault != null && !existingDefault.getId().equals(smtp.getId())) {
            existingDefault.setIsDefault(false);
            emailSmtpRepo.save(existingDefault);
          }
        }
      }

      @Override
      protected EmailSmtp process() {
        if (smtp.getId() == null) {
          // New SMTP config, set as default if no default exists
          if (emailSmtpRepo.findByIsDefaultTrue() == null) {
            smtp.setIsDefault(true);
          }
          insert(smtp);
        } else {
          // Update existing
          EmailSmtp existing = emailSmtpRepo.findById(smtp.getId()).orElse(smtp);
          update(smtp, existing);
          return existing;
        }
        return smtp;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<EmailSmtp, Long> getRepository() {
    return emailSmtpRepo;
  }
}

