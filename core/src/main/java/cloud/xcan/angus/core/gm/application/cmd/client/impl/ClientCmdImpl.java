package cloud.xcan.angus.core.gm.application.cmd.client.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.gm.application.converter.ClientConverter.getSystemTokenClientId;
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
import cloud.xcan.angus.core.gm.application.cmd.client.ClientCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.query.client.ClientQuery;
import cloud.xcan.angus.security.authentication.service.JdbcOAuth2AuthorizationService;
import cloud.xcan.angus.security.client.CustomOAuth2ClientRepository;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Biz
public class ClientCmdImpl implements ClientCmd {

  @Resource
  private ClientQuery clientQuery;

  @Resource
  private CustomOAuth2ClientRepository customOAuth2ClientRepository;

  @Resource
  private JdbcOAuth2AuthorizationService oauth2AuthorizationService;

  @Resource
  private OperationLogCmd operationLogCmd;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<String, Object> add(CustomOAuth2RegisteredClient client) {
    return new BizTemplate<IdKey<String, Object>>() {
      @Override
      protected void checkParams() {
        // Check the client existed
        RegisteredClient clientDb = customOAuth2ClientRepository.findByClientId(
            client.getClientId());
        assertResourceExisted(clientDb, client.getClientId(), "Client");
      }

      @Override
      protected IdKey<String, Object> process() {
        customOAuth2ClientRepository.save(client);
        operationLogCmd.add(AUTH_CLIENT, client, CREATED);
        return IdKey.of(client.getId(), client.getClientId());
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(CustomOAuth2RegisteredClient client) {
    new BizTemplate<Void>() {
      CustomOAuth2RegisteredClient clientDb;

      @Override
      protected void checkParams() {
        // Check the client existed
        clientDb = clientQuery.detail(client.getId());
      }

      @Override
      protected Void process() {
        customOAuth2ClientRepository.save(copyPropertiesIgnoreNull(client, clientDb));
        operationLogCmd.add(AUTH_CLIENT, clientDb, UPDATED);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<String, Object> replace(CustomOAuth2RegisteredClient client) {
    return new BizTemplate<IdKey<String, Object>>() {
      CustomOAuth2RegisteredClient clientDb;

      @Override
      protected void checkParams() {
        if (nonNull(client.getClientId())) {
          // Check the client existed
          clientDb = clientQuery.detail(client.getId());
        }
      }

      @Override
      protected IdKey<String, Object> process() {
        if (isNull(client.getId())) {
          return add(client);
        }

        customOAuth2ClientRepository.save(copyProperties(client, clientDb, false,
            "clientId", "clientIdIssuedAt", "platform", "source", "tenantId", "tenantName",
            "createdBy", "createdDate"));

        operationLogCmd.add(AUTH_CLIENT, clientDb, UPDATED);

        return IdKey.of(clientDb.getId(), clientDb.getClientId());
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(HashSet<String> clientIds) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        for (String clientId : clientIds) {
          CustomOAuth2RegisteredClient clientDb = (CustomOAuth2RegisteredClient)
              customOAuth2ClientRepository.findByClientId(clientId);

          customOAuth2ClientRepository.deleteByClientId(clientId);
          oauth2AuthorizationService.removeByClientId(clientId);

          operationLogCmd.add(AUTH_CLIENT, clientDb, DELETED);
        }
        return null;
      }
    }.execute();
  }

  @Override
  public void deleteSystemTokenClient(String tokenName, ClientSource source) {
    customOAuth2ClientRepository.deleteByClientId(getSystemTokenClientId(tokenName, source));
  }

}
