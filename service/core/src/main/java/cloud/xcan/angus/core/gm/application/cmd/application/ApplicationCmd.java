package cloud.xcan.angus.core.gm.application.cmd.application;

import cloud.xcan.angus.core.gm.domain.application.Application;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationStatus;

/**
 * Application Command Service
 */
public interface ApplicationCmd {

    /**
     * Create application
     */
    Application create(Application application);

    /**
     * Update application
     */
    Application update(Application application);

    /**
     * Update application status
     */
    void updateStatus(Long id, ApplicationStatus status);

    /**
     * Enable application
     */
    void enable(Long id);

    /**
     * Disable application
     */
    void disable(Long id);

    /**
     * Delete application
     */
    void delete(Long id);
}
