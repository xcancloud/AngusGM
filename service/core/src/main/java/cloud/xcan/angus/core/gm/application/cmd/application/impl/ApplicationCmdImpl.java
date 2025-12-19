package cloud.xcan.angus.core.gm.application.cmd.application.impl;

import cloud.xcan.angus.core.gm.application.cmd.application.ApplicationCmd;
import cloud.xcan.angus.core.gm.domain.application.Application;
import cloud.xcan.angus.core.gm.domain.application.ApplicationRepo;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationStatus;
import cloud.xcan.grapefruit.infra.exception.ResourceExisted;
import cloud.xcan.grapefruit.infra.exception.ResourceNotFound;
import cloud.xcan.grapefruit.infra.template.BizTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Application Command Service Implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationCmdImpl implements ApplicationCmd {

    private final ApplicationRepo applicationRepo;
    private final BizTemplate bizTemplate;

    @Override
    @Transactional
    public Application create(Application application) {
        return bizTemplate.execute(() -> {
            // Check if client ID already exists
            if (application.getClientId() != null && applicationRepo.findByClientId(application.getClientId()).isPresent()) {
                throw new ResourceExisted("Application with clientId already exists");
            }

            // Generate OAuth 2.0 credentials
            if (application.getClientId() == null) {
                application.setClientId(UUID.randomUUID().toString());
            }
            if (application.getClientSecret() == null) {
                application.setClientSecret(UUID.randomUUID().toString());
            }

            // Set default status
            application.setStatus(ApplicationStatus.ENABLED);
            application.setServiceCount(0);

            return applicationRepo.save(application);
        });
    }

    @Override
    @Transactional
    public Application update(Application application) {
        return bizTemplate.execute(() -> {
            Application existing = applicationRepo.findById(application.getId())
                    .orElseThrow(() -> new ResourceNotFound("Application not found"));

            return applicationRepo.save(application);
        });
    }

    @Override
    @Transactional
    public void enable(String id) {
        bizTemplate.execute(() -> {
            Application application = applicationRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFound("Application not found"));
            application.setStatus(ApplicationStatus.ENABLED);
            applicationRepo.save(application);
            return null;
        });
    }

    @Override
    @Transactional
    public void disable(String id) {
        bizTemplate.execute(() -> {
            Application application = applicationRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFound("Application not found"));
            application.setStatus(ApplicationStatus.DISABLED);
            applicationRepo.save(application);
            return null;
        });
    }

    @Override
    @Transactional
    public void delete(String id) {
        bizTemplate.execute(() -> {
            Application application = applicationRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFound("Application not found"));
            applicationRepo.delete(application);
            return null;
        });
    }
}
