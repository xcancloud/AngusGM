package cloud.xcan.angus.core.gm.application.query.application;

import cloud.xcan.angus.core.gm.domain.application.Application;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Application Query Service
 */
public interface ApplicationQuery {

    /**
     * Find application by ID
     */
    Application findById(Long id);

    /**
     * Find applications with pagination
     */
    Page<Application> find(GenericSpecification<Application> spec, PageRequest pageable,
                          boolean fullTextSearch, String[] match);

    /**
     * Get application statistics
     */
    Map<String, Object> getStats();
}
