package cloud.xcan.angus.core.gm.application.query.email.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotEmpty;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.NO_SEVER_AVAILABLE;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.NO_SEVER_AVAILABLE_T;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.SERVER_NAME_EXISTED_EXISTED_T;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.SERVER_OVER_LIMIT_CODE;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.SERVER_OVER_LIMIT_T;
import static cloud.xcan.angus.core.gm.infra.mail.EmailSender.setupServerConfig;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.EmailConstant;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.exception.QuotaException;
import cloud.xcan.angus.core.gm.application.query.email.EmailServerQuery;
import cloud.xcan.angus.core.gm.domain.email.server.EmailProtocol;
import cloud.xcan.angus.core.gm.domain.email.server.EmailServer;
import cloud.xcan.angus.core.gm.domain.email.server.EmailServerRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;


@Biz
@Slf4j
public class EmailServerQueryImpl implements EmailServerQuery {

  @Resource
  private EmailServerRepo emailServerRepo;

  private EmailServer cachedEmailServer;

  private Boolean cachedHealthResult;

  @Override
  public EmailServer detail(Long id) {
    return new BizTemplate<EmailServer>() {
      EmailServer server;

      @Override
      protected void checkParams() {
        server = checkAndFind(id);
      }

      @Override
      protected EmailServer process() {
        return server;
      }
    }.execute();
  }

  @Override
  public Page<EmailServer> find(Specification<EmailServer> spec, PageRequest pageable) {
    return new BizTemplate<Page<EmailServer>>() {

      @Override
      protected Page<EmailServer> process() {
        return emailServerRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public void checkEnable(EmailProtocol protocol) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        if (nonNull(protocol)) {
          List<EmailServer> servers = emailServerRepo.findAllByProtocolAndEnabled(protocol, true);
          assertNotEmpty(servers, NO_SEVER_AVAILABLE_T, new Object[]{protocol.getValue()});
        } else {
          List<EmailServer> servers = emailServerRepo.findByEnabled(true);
          assertNotEmpty(servers, NO_SEVER_AVAILABLE);
        }
        return null;
      }
    }.execute();
  }

  @DoInFuture("Support the receiving protocol: POP3, IMAP")
  @Override
  public Boolean checkHealth(EmailProtocol protocol) {
    return new BizTemplate<Boolean>() {

      @Override
      protected Boolean process() {
        try {
          EmailServer enabledEmailServer = findEnabled0(protocol);
          if (Objects.isNull(enabledEmailServer)) {
            cachedHealthResult = false;
            return false;
          }
          // Configuration unchanged
          // If you want to trigger modification, you need to modify the server
          if (enabledEmailServer.equals(cachedEmailServer)) {
            return cachedHealthResult;
          }
          // Fix:: Authentication failed; nested exception is jakarta.mail.AuthenticationFailedException: 451 4.3.2 Temporary authentication failure (rate-limit)
          cachedEmailServer = enabledEmailServer;
          //Get the Transport object
          Transport transport = setupServerConfig(enabledEmailServer, 5 * 1000, 5 * 1000)
              .getTransport();
          //connect mail smtp emailServer
          transport.connect();
          cachedHealthResult = transport.isConnected();
          return cachedHealthResult;
        } catch (MessagingException e) {
          log.error("Try connect {} mail server error: {}", protocol.getValue(), e.getMessage());
          throw new RuntimeException(e.getMessage());
        }
      }
    }.execute();
  }

  @Override
  public EmailServer checkAndFind(Long id) {
    return emailServerRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(String.valueOf(id), "MailServer"));
  }

  @Override
  public EmailServer findEnabled(EmailProtocol protocol) {
    List<EmailServer> enabledEmailServers = emailServerRepo
        .findAllByProtocolAndEnabled(protocol, true);
    assertNotEmpty(enabledEmailServers, NO_SEVER_AVAILABLE_T,
        new Object[]{EmailProtocol.SMTP.getValue()});
    return enabledEmailServers.get(0);
  }

  @Override
  public EmailServer findEnabled0(EmailProtocol protocol) {
    List<EmailServer> enabledEmailServers = emailServerRepo
        .findAllByProtocolAndEnabled(protocol, true);
    return isEmpty(enabledEmailServers) ? null : enabledEmailServers.get(0);
  }

  @Override
  public void checkQuota(int incr) {
    long count = emailServerRepo.count();
    if (count + incr > EmailConstant.MAX_MAIL_SERVER_QUOTA) {
      throw QuotaException.of(SERVER_OVER_LIMIT_CODE, SERVER_OVER_LIMIT_T,
          new Object[]{EmailConstant.MAX_MAIL_SERVER_QUOTA});
    }
  }

  @Override
  public void checkAddName(EmailServer emailServer) {
    List<EmailServer> servers = emailServerRepo.findByName(emailServer.getName());
    assertResourceExisted(servers, SERVER_NAME_EXISTED_EXISTED_T,
        new Object[]{emailServer.getName()});
  }

  @Override
  public void checkUpdateName(EmailServer emailServer) {
    List<EmailServer> servers = emailServerRepo.findByNameAndIdNot(emailServer.getName(),
        emailServer.getId());
    assertResourceExisted(servers, SERVER_NAME_EXISTED_EXISTED_T,
        new Object[]{emailServer.getName()});
  }
}
