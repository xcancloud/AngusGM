package cloud.xcan.angus.core.gm.application.cmd.auth.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.gm.application.converter.AuthClientConverter.getSystemTokenClientId;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.AUTH_CLIENT;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATED;
import static cloud.xcan.angus.core.utils.CoreUtils.copyProperties;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreNull;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.client.ClientSource;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.auth.AuthClientCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.query.auth.AuthClientQuery;
import cloud.xcan.angus.security.authentication.service.JdbcOAuth2AuthorizationService;
import cloud.xcan.angus.security.client.CustomOAuth2ClientRepository;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of OAuth2 client command operations for managing OAuth2 clients.
 * 
 * <p>This class provides comprehensive functionality for OAuth2 client management including:</p>
 * <ul>
 *   <li>Creating, updating, and deleting OAuth2 clients</li>
 *   <li>Managing client configurations and credentials</li>
 *   <li>Handling system token client operations</li>
 *   <li>Recording operation logs for audit trails</li>
 * </ul>
 * 
 * <p>The implementation ensures proper OAuth2 client lifecycle management
 * and maintains authorization service consistency.</p>
 */
@Slf4j
@Biz
public class AuthClientCmdImpl implements AuthClientCmd {

  @Resource
  private AuthClientQuery authClientQuery;
  @Resource
  private CustomOAuth2ClientRepository customOAuth2ClientRepository;
  @Resource
  private JdbcOAuth2AuthorizationService oauth2AuthorizationService;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * Creates a new OAuth2 client with validation and audit logging.
   * 
   * <p>This method performs comprehensive client creation including:</p>
   * <ul>
   *   <li>Validating that client does not already exist</li>
   *   <li>Saving client configuration to repository</li>
   *   <li>Recording creation audit logs</li>
   * </ul>
   * 
   * @param client OAuth2 client entity to create
   * @return Client identifier with associated data
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<String, Object> add(CustomOAuth2RegisteredClient client) {
    return new BizTemplate<IdKey<String, Object>>() {
      @Override
      protected void checkParams() {
        // Validate that client does not already exist
        RegisteredClient clientDb = customOAuth2ClientRepository.findByClientId(
            client.getClientId());
        assertResourceExisted(clientDb, client.getClientId(), "Client");
      }

      @Override
      protected IdKey<String, Object> process() {
        // Save client to repository
        customOAuth2ClientRepository.save(client);
        // Record creation audit log
        operationLogCmd.add(AUTH_CLIENT, client, CREATED);
        return IdKey.of(client.getId(), client.getClientId());
      }
    }.execute();
  }

  /**
   * Updates an existing OAuth2 client with new configuration.
   * 
   * <p>This method ensures data consistency by:</p>
   * <ul>
   *   <li>Validating that client exists before update</li>
   *   <li>Preserving immutable fields during update</li>
   *   <li>Recording update audit logs</li>
   * </ul>
   * 
   * @param client OAuth2 client entity with updated configuration
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(CustomOAuth2RegisteredClient client) {
    new BizTemplate<Void>() {
      CustomOAuth2RegisteredClient clientDb;

      @Override
      protected void checkParams() {
        // Validate that client exists
        clientDb = authClientQuery.detail(client.getId());
      }

      @Override
      protected Void process() {
        // Update client with null-safe property copying
        customOAuth2ClientRepository.save(copyPropertiesIgnoreNull(client, clientDb));
        // Record update audit log
        operationLogCmd.add(AUTH_CLIENT, clientDb, UPDATED);
        return null;
      }
    }.execute();
  }

  /**
   * Replaces an OAuth2 client by creating new or updating existing.
   * 
   * <p>This method handles both creation and update scenarios:</p>
   * <ul>
   *   <li>Creates new client if no ID is provided</li>
   *   <li>Updates existing client if ID is provided</li>
   *   <li>Preserves immutable fields during replacement</li>
   *   <li>Maintains audit trails for all operations</li>
   * </ul>
   * 
   * @param client OAuth2 client entity to replace
   * @return Client identifier with associated data
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<String, Object> replace(CustomOAuth2RegisteredClient client) {
    return new BizTemplate<IdKey<String, Object>>() {
      CustomOAuth2RegisteredClient clientDb;

      @Override
      protected void checkParams() {
        if (nonNull(client.getClientId())) {
          // Validate that client exists if updating
          clientDb = authClientQuery.detail(client.getId());
        }
      }

      @Override
      protected IdKey<String, Object> process() {
        if (isNull(client.getId())) {
          return add(client);
        }

        // Update client while preserving immutable fields
        customOAuth2ClientRepository.save(copyProperties(client, clientDb, false,
            "clientId", "clientIdIssuedAt", "platform", "source", "tenantId", "tenantName",
            "createdBy", "createdDate"));

        // Record update audit log
        operationLogCmd.add(AUTH_CLIENT, clientDb, UPDATED);

        return IdKey.of(clientDb.getId(), clientDb.getClientId());
      }
    }.execute();
  }

  /**
   * Deletes OAuth2 clients and cleans up related authorization data.
   * 
   * <p>This method performs comprehensive cleanup including:</p>
   * <ul>
   *   <li>Removing client configurations from repository</li>
   *   <li>Cleaning up authorization service data</li>
   *   <li>Recording deletion audit logs</li>
   * </ul>
   * 
   * @param clientIds Set of client identifiers to delete
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(HashSet<String> clientIds) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        for (String clientId : clientIds) {
          // Find client for audit logging
          CustomOAuth2RegisteredClient clientDb = (CustomOAuth2RegisteredClient)
              customOAuth2ClientRepository.findByClientId(clientId);

          // Delete client from repository
          customOAuth2ClientRepository.deleteByClientId(clientId);
          // Clean up authorization service data
          oauth2AuthorizationService.removeByClientId(clientId);

          // Record deletion audit log
          operationLogCmd.add(AUTH_CLIENT, clientDb, DELETED);
        }
        return null;
      }
    }.execute();
  }

  /**
   * Deletes system token client by name and source.
   * 
   * <p>This method removes system token clients used for internal
   * authentication purposes.</p>
   * 
   * @param tokenName Name of the system token
   * @param source Source of the client
   */
  @Override
  public void deleteSystemTokenClient(String tokenName, ClientSource source) {
    customOAuth2ClientRepository.deleteByClientId(getSystemTokenClientId(tokenName, source));
  }

}
