package cloud.xcan.angus.core.gm.application.cmd.email.impl;

import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.EMAIL_SERVER;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DISABLED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ENABLED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATED;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreNull;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailServerCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.query.email.EmailServerQuery;
import cloud.xcan.angus.core.gm.domain.email.server.EmailServer;
import cloud.xcan.angus.core.gm.domain.email.server.EmailServerRepo;
import cloud.xcan.angus.core.gm.infra.mail.EmailSender;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;


@Biz
public class EmailServerCmdImpl extends CommCmd<EmailServer, Long> implements EmailServerCmd {

  @Resource
  private EmailServerRepo emailServerRepo;

  @Resource
  private EmailServerQuery emailServerQuery;

  @Resource
  private EmailSender emailSender;

  @Resource
  private OperationLogCmd operationLogCmd;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(EmailServer server) {
    return new BizTemplate<IdKey<Long, Object>>() {

      @Override
      protected void checkParams() {
        emailServerQuery.checkAddName(server);
        emailServerQuery.checkQuota(1);
      }

      @Override
      protected IdKey<Long, Object> process() {
        // If there is no enabled mail server, set the current state to enable.
        List<EmailServer> enabledServers = emailServerRepo
            .findAllByProtocolAndEnabled(server.getProtocol(), true);
        server.setEnabled(isEmpty(enabledServers));

        // Save email server
        server.setId(uidGenerator.getUID());
        emailServerRepo.save(server);

        // Update sender instance
        if (server.isValidSmtpServer()) {
          emailSender.initOrRefreshEmailSender(server);
        }

        // Save operation log
        operationLogCmd.add(EMAIL_SERVER, server, CREATED);
        return IdKey.of(server.getId(), server.getHost() + ":" + server.getPort());
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(EmailServer server) {
    new BizTemplate<Void>() {
      EmailServer serverDb;

      @Override
      protected void checkParams() {
        serverDb = emailServerQuery.checkAndFind(server.getId());
        if (isNotEmpty(server.getName())) {
          emailServerQuery.checkUpdateName(server);
        }
      }

      @Override
      protected Void process() {
        emailServerRepo.save(copyPropertiesIgnoreNull(server, serverDb));

        // Save operation log
        operationLogCmd.add(EMAIL_SERVER, serverDb, UPDATED);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> replace(EmailServer server) {
    return new BizTemplate<IdKey<Long, Object>>() {
      EmailServer serverDb;

      @Override
      protected void checkParams() {
        if (nonNull(server.getId())) {
          serverDb = emailServerQuery.checkAndFind(server.getId());
        }

        // Check the name of replaced emailServer
        if (nonNull(serverDb)) {
          emailServerQuery.checkUpdateName(server);
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Add email server
        if (isNull(serverDb)) {
          return add(server);
        }

        // Replace email server
        server.setEnabled(serverDb.getEnabled());
        emailServerRepo.save(server);

        // Update sender instance
        if (server.isValidSmtpServer()) {
          emailSender.setupMailSender(server);
        }

        // Save operation log
        operationLogCmd.add(EMAIL_SERVER, serverDb, UPDATED);
        return IdKey.of(server.getId(), server.getHost() + ":" + server.getPort());
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(HashSet<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        List<EmailServer> servers = emailServerRepo.findAllById(ids);
        if (!servers.isEmpty()) {
          emailServerRepo.deleteByIdIn(ids);
          operationLogCmd.addAll(EMAIL_SERVER, servers, DELETED);
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(Long id, Boolean enabled) {
    new BizTemplate<Void>() {
      EmailServer serverDb;

      @Override
      protected void checkParams() {
        serverDb = emailServerQuery.checkAndFind(id);
      }

      @Override
      protected Void process() {
        if (serverDb.getEnabled().equals(enabled)) {
          return null;
        }

        //  Disable current when enabled = false
        if (!enabled) {
          serverDb.setEnabled(false);
          emailServerRepo.save(serverDb);
          operationLogCmd.add(EMAIL_SERVER, serverDb, DISABLED);
          return null;
        }

        serverDb.setEnabled(true);
        emailServerRepo.save(serverDb);

        List<EmailServer> otherEmailServers = emailServerRepo
            .findAllByProtocolAndIdNot(serverDb.getProtocol(), id);
        if (isNotEmpty(otherEmailServers)) {
          // Only one server is enabled
          for (EmailServer otherEmailServer : otherEmailServers) {
            otherEmailServer.setEnabled(false);
          }
          emailServerRepo.batchUpdate(otherEmailServers);
        }

        operationLogCmd.add(EMAIL_SERVER, serverDb, ENABLED);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<EmailServer, Long> getRepository() {
    return this.emailServerRepo;
  }
}
