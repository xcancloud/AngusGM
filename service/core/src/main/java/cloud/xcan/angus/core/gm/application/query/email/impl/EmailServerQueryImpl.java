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

/**
 * <p>
 * Implementation of email server query operations.
 * </p>
 * <p>
 * Manages email server retrieval, health checking, and quota validation. Provides comprehensive
 * email server querying with caching support.
 * </p>
 * <p>
 * Supports server detail retrieval, health monitoring, quota validation, and name uniqueness
 * checking for comprehensive email server management.
 * </p>
 */
@org.springframework.stereotype.Service
@Slf4j
public class EmailServerQueryImpl implements EmailServerQuery {

  @Resource
  private EmailServerRepo emailServerRepo;

  private EmailServer cachedEmailServer;

  private Boolean cachedHealthResult;

  /**
   * <p>
   * Retrieves detailed email server information by ID.
   * </p>
   * <p>
   * Fetches complete server record with validation. Throws ResourceNotFound exception if server
   * does not exist.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves email servers with specification-based filtering.
   * </p>
   * <p>
   * Supports dynamic filtering and pagination for comprehensive server management.
   * </p>
   */
  @Override
  public Page<EmailServer> list(Specification<EmailServer> spec, PageRequest pageable) {
    return new BizTemplate<Page<EmailServer>>() {

      @Override
      protected Page<EmailServer> process() {
        return emailServerRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  /**
   * <p>
   * Validates email server availability for specified protocol.
   * </p>
   * <p>
   * Verifies that enabled servers exist for the specified protocol. Throws appropriate exception if
   * no servers are available.
   * </p>
   */
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

  /**
   * <p>
   * Validates email server health status for specified protocol.
   * </p>
   * <p>
   * Tests server connectivity with caching for performance optimization. Supports future POP3 and
   * IMAP protocol implementations.
   * </p>
   */
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
          // Fix: Authentication failed; nested exception is jakarta.mail.AuthenticationFailedException: 451 4.3.2 Temporary authentication failure (rate-limit)
          cachedEmailServer = enabledEmailServer;
          // Get the Transport object
          Transport transport = setupServerConfig(enabledEmailServer,
              5 * 1000, 5 * 1000).getTransport();
          // Connect mail SMTP email server
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

  /**
   * <p>
   * Validates and retrieves email server by ID.
   * </p>
   * <p>
   * Verifies server exists and returns server information. Throws ResourceNotFound exception if
   * server does not exist.
   * </p>
   */
  @Override
  public EmailServer checkAndFind(Long id) {
    return emailServerRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(String.valueOf(id), "MailServer"));
  }

  /**
   * <p>
   * Retrieves enabled email server for specified protocol.
   * </p>
   * <p>
   * Returns first enabled server for the protocol with validation. Throws appropriate exception if
   * no enabled servers found.
   * </p>
   */
  @Override
  public EmailServer findEnabled(EmailProtocol protocol) {
    List<EmailServer> enabledEmailServers = emailServerRepo
        .findAllByProtocolAndEnabled(protocol, true);
    assertNotEmpty(enabledEmailServers, NO_SEVER_AVAILABLE_T,
        new Object[]{EmailProtocol.SMTP.getValue()});
    return enabledEmailServers.get(0);
  }

  /**
   * <p>
   * Retrieves enabled email server for specified protocol without validation.
   * </p>
   * <p>
   * Returns first enabled server for the protocol or null if none found.
   * </p>
   */
  @Override
  public EmailServer findEnabled0(EmailProtocol protocol) {
    List<EmailServer> enabledEmailServers = emailServerRepo
        .findAllByProtocolAndEnabled(protocol, true);
    return isEmpty(enabledEmailServers) ? null : enabledEmailServers.get(0);
  }

  /**
   * <p>
   * Validates email server quota for tenant.
   * </p>
   * <p>
   * Checks if adding servers would exceed maximum quota limits. Throws appropriate exception if
   * quota would be exceeded.
   * </p>
   */
  @Override
  public void checkQuota(int incr) {
    long count = emailServerRepo.count();
    if (count + incr > EmailConstant.MAX_MAIL_SERVER_QUOTA) {
      throw QuotaException.of(SERVER_OVER_LIMIT_CODE, SERVER_OVER_LIMIT_T,
          new Object[]{EmailConstant.MAX_MAIL_SERVER_QUOTA});
    }
  }

  /**
   * <p>
   * Validates server name uniqueness for new servers.
   * </p>
   * <p>
   * Checks if server name already exists. Throws ResourceExisted exception if name is not unique.
   * </p>
   */
  @Override
  public void checkAddName(EmailServer emailServer) {
    List<EmailServer> servers = emailServerRepo.findByName(emailServer.getName());
    assertResourceExisted(servers, SERVER_NAME_EXISTED_EXISTED_T,
        new Object[]{emailServer.getName()});
  }

  /**
   * <p>
   * Validates server name uniqueness for updated servers.
   * </p>
   * <p>
   * Checks if server name conflicts with existing servers. Allows same name for the same server
   * during updates.
   * </p>
   */
  @Override
  public void checkUpdateName(EmailServer emailServer) {
    List<EmailServer> servers = emailServerRepo.findByNameAndIdNot(emailServer.getName(),
        emailServer.getId());
    assertResourceExisted(servers, SERVER_NAME_EXISTED_EXISTED_T,
        new Object[]{emailServer.getName()});
  }
}
