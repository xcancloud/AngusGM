package cloud.xcan.angus.core.gm.application.query.application;

import cloud.xcan.angus.core.gm.domain.application.Application;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationFindDto;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * Application Query Service
 */
public interface ApplicationQuery {

    /**
     * Find application by ID
     */
    Application findById(String id);

    /**
     * Find applications
     */
    Page<Application> find(ApplicationFindDto dto);

    /**
     * Get application statistics
     */
    Map<String, Object> getStats();
}
