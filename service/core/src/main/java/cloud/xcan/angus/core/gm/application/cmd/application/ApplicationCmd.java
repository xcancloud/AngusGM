package cloud.xcan.angus.core.gm.application.cmd.application;

import cloud.xcan.angus.core.gm.domain.application.Application;

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
     * Enable application
     */
    void enable(String id);

    /**
     * Disable application
     */
    void disable(String id);

    /**
     * Delete application
     */
    void delete(String id);
}
