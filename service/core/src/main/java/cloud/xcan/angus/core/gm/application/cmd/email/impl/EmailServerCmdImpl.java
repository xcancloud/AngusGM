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

/**
 * Implementation of email server command operations for managing email server configurations.
 *
 * <p>This class provides comprehensive functionality for email server management including:</p>
 * <ul>
 *   <li>Creating and configuring email servers</li>
 *   <li>Managing server enabled/disabled status</li>
 *   <li>Updating server configurations</li>
 *   <li>Ensuring only one server per protocol is enabled</li>
 *   <li>Recording operation logs for audit trails</li>
 * </ul>
 *
 * <p>The implementation ensures proper email server management with automatic
 * sender instance updates and audit trail maintenance.</p>
 */
@org.springframework.stereotype.Service
public class EmailServerCmdImpl extends CommCmd<EmailServer, Long> implements EmailServerCmd {

  @Resource
  private EmailServerRepo emailServerRepo;
  @Resource
  private EmailServerQuery emailServerQuery;
  @Resource
  private EmailSender emailSender;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * Creates a new email server with automatic enabled status management.
   *
   * <p>This method performs server creation including:</p>
   * <ul>
   *   <li>Validating server name uniqueness</li>
   *   <li>Checking server quota limits</li>
   *   <li>Setting enabled status (auto-enabled if no other enabled servers)</li>
   *   <li>Updating email sender instance</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   *
   * @param server Email server configuration to create
   * @return Created server identifier with host:port information
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(EmailServer server) {
    return new BizTemplate<IdKey<Long, Object>>() {

      @Override
      protected void checkParams() {
        // Validate server name uniqueness
        emailServerQuery.checkAddName(server);
        // Validate server quota
        emailServerQuery.checkQuota(1);
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Auto-enable if no other enabled servers exist for this protocol
        List<EmailServer> enabledServers = emailServerRepo
            .findAllByProtocolAndEnabled(server.getProtocol(), true);
        server.setEnabled(isEmpty(enabledServers));

        // Save email server configuration
        server.setId(uidGenerator.getUID());
        emailServerRepo.save(server);

        // Update email sender instance for SMTP servers
        if (server.isValidSmtpServer()) {
          emailSender.initOrRefreshEmailSender(server);
        }

        // Record operation audit log
        operationLogCmd.add(EMAIL_SERVER, server, CREATED);
        return IdKey.of(server.getId(), server.getHost() + ":" + server.getPort());
      }
    }.execute();
  }

  /**
   * Updates an existing email server configuration.
   *
   * <p>This method performs server update including:</p>
   * <ul>
   *   <li>Validating server existence</li>
   *   <li>Checking name uniqueness if name is being changed</li>
   *   <li>Updating server configuration</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   *
   * @param server Email server configuration to update
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(EmailServer server) {
    new BizTemplate<Void>() {
      EmailServer serverDb;

      @Override
      protected void checkParams() {
        // Validate server exists
        serverDb = emailServerQuery.checkAndFind(server.getId());
        // Validate name uniqueness if name is being changed
        if (isNotEmpty(server.getName())) {
          emailServerQuery.checkUpdateName(server);
        }
      }

      @Override
      protected Void process() {
        // Update server configuration
        emailServerRepo.save(copyPropertiesIgnoreNull(server, serverDb));

        // Record operation audit log
        operationLogCmd.add(EMAIL_SERVER, serverDb, UPDATED);
        return null;
      }
    }.execute();
  }

  /**
   * Replaces an email server configuration or creates a new one.
   *
   * <p>This method performs server replacement including:</p>
   * <ul>
   *   <li>Validating server existence if ID is provided</li>
   *   <li>Checking name uniqueness</li>
   *   <li>Creating new server or updating existing one</li>
   *   <li>Updating email sender instance</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   *
   * @param server Email server configuration to replace
   * @return Server identifier with host:port information
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> replace(EmailServer server) {
    return new BizTemplate<IdKey<Long, Object>>() {
      EmailServer serverDb;

      @Override
      protected void checkParams() {
        // Validate server exists if ID is provided
        if (nonNull(server.getId())) {
          serverDb = emailServerQuery.checkAndFind(server.getId());
        }

        // Validate name uniqueness for replacement
        if (nonNull(serverDb)) {
          emailServerQuery.checkUpdateName(server);
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Create new server if no existing server found
        if (isNull(serverDb)) {
          return add(server);
        }

        // Replace existing server configuration
        server.setEnabled(serverDb.getEnabled());
        emailServerRepo.save(server);

        // Update email sender instance for SMTP servers
        if (server.isValidSmtpServer()) {
          emailSender.setupMailSender(server);
        }

        // Record operation audit log
        operationLogCmd.add(EMAIL_SERVER, serverDb, UPDATED);
        return IdKey.of(server.getId(), server.getHost() + ":" + server.getPort());
      }
    }.execute();
  }

  /**
   * Deletes email servers by their identifiers.
   *
   * <p>This method performs server deletion including:</p>
   * <ul>
   *   <li>Retrieving server information for audit logs</li>
   *   <li>Deleting server configurations</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   *
   * @param ids Set of server identifiers to delete
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(HashSet<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        // Retrieve servers for audit logging
        List<EmailServer> servers = emailServerRepo.findAllById(ids);
        if (!servers.isEmpty()) {
          // Delete server configurations
          emailServerRepo.deleteByIdIn(ids);
          // Record operation audit logs
          operationLogCmd.addAll(EMAIL_SERVER, servers, DELETED);
        }
        return null;
      }
    }.execute();
  }

  /**
   * Enables or disables an email server with automatic status management.
   *
   * <p>This method manages server enabled status including:</p>
   * <ul>
   *   <li>Validating server existence</li>
   *   <li>Managing enabled/disabled status</li>
   *   <li>Ensuring only one server per protocol is enabled</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   *
   * @param id      Server identifier
   * @param enabled Whether to enable the server
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(Long id, Boolean enabled) {
    new BizTemplate<Void>() {
      EmailServer serverDb;

      @Override
      protected void checkParams() {
        // Validate server exists
        serverDb = emailServerQuery.checkAndFind(id);
      }

      @Override
      protected Void process() {
        // Skip if status is already set correctly
        if (serverDb.getEnabled().equals(enabled)) {
          return null;
        }

        // Disable server when enabled = false
        if (!enabled) {
          serverDb.setEnabled(false);
          emailServerRepo.save(serverDb);
          operationLogCmd.add(EMAIL_SERVER, serverDb, DISABLED);
          return null;
        }

        // Enable server and disable others for same protocol
        serverDb.setEnabled(true);
        emailServerRepo.save(serverDb);

        // Ensure only one server per protocol is enabled
        List<EmailServer> otherEmailServers = emailServerRepo
            .findAllByProtocolAndIdNot(serverDb.getProtocol(), id);
        if (isNotEmpty(otherEmailServers)) {
          // Disable other servers of the same protocol
          for (EmailServer otherEmailServer : otherEmailServers) {
            otherEmailServer.setEnabled(false);
          }
          emailServerRepo.batchUpdate(otherEmailServers);
        }

        // Record operation audit log
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
