package cloud.xcan.angus.core.gm.application.cmd.application.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.application.ApplicationCmd;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.domain.application.Application;
import cloud.xcan.angus.core.gm.domain.application.ApplicationRepo;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application Command Service Implementation
 */
@Biz
public class ApplicationCmdImpl extends CommCmd<Application, Long> implements ApplicationCmd {

    @Resource
    private ApplicationRepo applicationRepo;

    @Resource
    private ApplicationQuery applicationQuery;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Application create(Application application) {
        return new BizTemplate<Application>() {
            @Override
            protected void checkParams() {
                if (application.getClientId() != null 
                    && applicationRepo.findByClientId(application.getClientId()).isPresent()) {
                    throw ResourceExisted.of("Application with clientId already exists", new Object[]{});
                }
            }

            @Override
            protected Application process() {
                // Generate OAuth 2.0 credentials
                if (application.getClientId() == null) {
                    application.setClientId(UUID.randomUUID().toString());
                }
                if (application.getClientSecret() == null) {
                    application.setClientSecret(UUID.randomUUID().toString());
                }

                // Set default status
                if (application.getStatus() == null) {
                    application.setStatus(ApplicationStatus.ENABLED);
                }
                if (application.getServiceCount() == null) {
                    application.setServiceCount(0L);
                }

                insert(application);
                return application;
            }
        }.execute();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Application update(Application application) {
        return new BizTemplate<Application>() {
            Application appDb;

            @Override
            protected void checkParams() {
                appDb = applicationQuery.findById(application.getId());
            }

            @Override
            protected Application process() {
                update(application, appDb);
                return appDb;
            }
        }.execute();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, ApplicationStatus status) {
        new BizTemplate<Void>() {
            Application appDb;

            @Override
            protected void checkParams() {
                appDb = applicationQuery.findById(id);
            }

            @Override
            protected Void process() {
                appDb.setStatus(status);
                applicationRepo.save(appDb);
                return null;
            }
        }.execute();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(Long id) {
        new BizTemplate<Void>() {
            Application appDb;

            @Override
            protected void checkParams() {
                appDb = applicationQuery.findById(id);
            }

            @Override
            protected Void process() {
                appDb.setStatus(ApplicationStatus.ENABLED);
                applicationRepo.save(appDb);
                return null;
            }
        }.execute();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disable(Long id) {
        new BizTemplate<Void>() {
            Application appDb;

            @Override
            protected void checkParams() {
                appDb = applicationQuery.findById(id);
            }

            @Override
            protected Void process() {
                appDb.setStatus(ApplicationStatus.DISABLED);
                applicationRepo.save(appDb);
                return null;
            }
        }.execute();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        new BizTemplate<Void>() {
            @Override
            protected void checkParams() {
                applicationQuery.findById(id);
            }

            @Override
            protected Void process() {
                applicationRepo.deleteById(id);
                return null;
            }
        }.execute();
    }

    @Override
    protected BaseRepository<Application, Long> getRepository() {
        return applicationRepo;
    }
}
