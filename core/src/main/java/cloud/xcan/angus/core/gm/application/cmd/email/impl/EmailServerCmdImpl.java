package cloud.xcan.angus.core.gm.application.cmd.email.impl;

import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreNull;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailServerCmd;
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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(EmailServer emailServer) {
    return new BizTemplate<IdKey<Long, Object>>() {

      @Override
      protected void checkParams() {
        emailServerQuery.checkAddName(emailServer);
        emailServerQuery.checkQuota(1);
      }

      @Override
      protected IdKey<Long, Object> process() {
        // If there is no enabled mail emailServer, set the current state to enabled.
        List<EmailServer> enabledServers = emailServerRepo
            .findAllByProtocolAndEnabled(emailServer.getProtocol(), true);
        emailServer.setEnabled(isEmpty(enabledServers));

        // Save email server
        emailServer.setId(uidGenerator.getUID());
        emailServerRepo.save(emailServer);

        // Update sender instance
        if (emailServer.isValidSmtpServer()) {
          emailSender.initOrRefreshEmailSender(emailServer);
        }
        return IdKey.of(emailServer.getId(), emailServer.getHost() + ":" + emailServer.getPort());
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(EmailServer emailServer) {
    new BizTemplate<Void>() {
      EmailServer emailServerDb;

      @Override
      protected void checkParams() {
        emailServerDb = emailServerQuery.checkAndFind(emailServer.getId());
        if (isNotEmpty(emailServer.getName())) {
          emailServerQuery.checkUpdateName(emailServer);
        }
      }

      @Override
      protected Void process() {
        emailServerRepo.save(copyPropertiesIgnoreNull(emailServer, emailServerDb));
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> replace(EmailServer emailServer) {
    return new BizTemplate<IdKey<Long, Object>>() {
      EmailServer emailServerDb;

      @Override
      protected void checkParams() {
        if (nonNull(emailServer.getId())) {
          emailServerDb = emailServerQuery.checkAndFind(emailServer.getId());
        }

        // Check the name of replaced emailServer
        if (nonNull(emailServerDb)) {
          emailServerQuery.checkUpdateName(emailServer);
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Add email server
        if (isNull(emailServerDb)) {
          return add(emailServer);
        }

        // Replace email server
        emailServer.setEnabled(emailServerDb.getEnabled());
        emailServerRepo.save(emailServer);

        // Update sender instance
        if (emailServer.isValidSmtpServer()) {
          emailSender.setupMailSender(emailServer);
        }
        return IdKey.of(emailServer.getId(), emailServer.getHost() + ":" + emailServer.getPort());
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(HashSet<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        emailServerRepo.deleteByIdIn(ids);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(Long id, Boolean enabled) {
    new BizTemplate<Void>() {
      EmailServer emailServerDb;

      @Override
      protected void checkParams() {
        emailServerDb = emailServerQuery.checkAndFind(id);
      }

      @Override
      protected Void process() {
        if (emailServerDb.getEnabled().equals(enabled)) {
          return null;
        }

        //  Disable current when enabled = false
        if (!enabled) {
          emailServerDb.setEnabled(false);
          emailServerRepo.save(emailServerDb);
          return null;
        }

        emailServerDb.setEnabled(true);
        emailServerRepo.save(emailServerDb);

        List<EmailServer> otherEmailServers = emailServerRepo
            .findAllByProtocolAndIdNot(emailServerDb.getProtocol(), id);
        if (isNotEmpty(otherEmailServers)) {
          // Only one server is enabled
          for (EmailServer otherEmailServer : otherEmailServers) {
            otherEmailServer.setEnabled(false);
          }
          emailServerRepo.batchUpdate(otherEmailServers);
        }

        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<EmailServer, Long> getRepository() {
    return this.emailServerRepo;
  }
}
