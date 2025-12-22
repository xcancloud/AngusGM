package cloud.xcan.angus.core.gm.application.query.email.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.email.EmailSmtpQuery;
import cloud.xcan.angus.core.gm.domain.email.EmailSmtp;
import cloud.xcan.angus.core.gm.domain.email.EmailSmtpRepo;
import jakarta.annotation.Resource;
import java.util.Optional;

/**
 * Implementation of email SMTP configuration query service
 */
@Biz
public class EmailSmtpQueryImpl implements EmailSmtpQuery {

  @Resource
  private EmailSmtpRepo emailSmtpRepo;

  @Override
  public Optional<EmailSmtp> findDefault() {
    return new BizTemplate<Optional<EmailSmtp>>() {
      @Override
      protected Optional<EmailSmtp> process() {
        EmailSmtp defaultSmtp = emailSmtpRepo.findByIsDefaultTrue();
        return Optional.ofNullable(defaultSmtp);
      }
    }.execute();
  }
}

