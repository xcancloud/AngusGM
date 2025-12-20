package cloud.xcan.angus.core.gm.infra.persistence.mysql.application;

import cloud.xcan.angus.core.gm.domain.application.Application;
import cloud.xcan.angus.core.gm.domain.application.ApplicationRepo;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationStatus;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationType;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Application Repository MySQL Implementation
 */
@Repository
@Profile("mysql")
public interface ApplicationRepoMysql extends ApplicationRepo, JpaRepository<Application, String> {

    @Override
    default Optional<Application> findByClientId(String clientId) {
        return findAll().stream()
                .filter(app -> clientId.equals(app.getClientId()))
                .findFirst();
    }

    @Override
    default Page<Application> findByStatus(ApplicationStatus status, Pageable pageable) {
        return findAll(pageable);
    }

    @Override
    default Page<Application> findByType(ApplicationType type, Pageable pageable) {
        return findAll(pageable);
    }

    @Override
    default Page<Application> findByStatusAndType(ApplicationStatus status, ApplicationType type, Pageable pageable) {
        return findAll(pageable);
    }

    @Override
    default Page<Application> findByOwnerId(String ownerId, Pageable pageable) {
        return findAll(pageable);
    }

    @Override
    default Long countByStatus(ApplicationStatus status) {
        return findAll().stream()
                .filter(app -> status.equals(app.getStatus()))
                .count();
    }

    @Override
    default Long countByType(ApplicationType type) {
        return findAll().stream()
                .filter(app -> type.equals(app.getType()))
                .count();
    }
}
