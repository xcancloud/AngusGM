package cloud.xcan.angus.core.gm.application.query.application.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.domain.application.Application;
import cloud.xcan.angus.core.gm.domain.application.ApplicationRepo;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationStatus;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationType;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Application Query Service Implementation
 */
@Biz
public class ApplicationQueryImpl implements ApplicationQuery {

    @Resource
    private ApplicationRepo applicationRepo;

    @Override
    public Application findById(Long id) {
        return new BizTemplate<Application>() {
            @Override
            protected Application process() {
                return applicationRepo.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("Application not found", new Object[]{}));
            }
        }.execute();
    }

    @Override
    public Page<Application> find(GenericSpecification<Application> spec, PageRequest pageable,
                                  boolean fullTextSearch, String[] match) {
        return new BizTemplate<Page<Application>>() {
            @Override
            protected Page<Application> process() {
                // Use standard repository query with specification
                // If full-text search is needed, it should be handled by SearchRepo
                // For now, use standard findAll with specification
                return applicationRepo.findAll(spec, pageable);
            }
        }.execute();
    }

    @Override
    public Map<String, Object> getStats() {
        return new BizTemplate<Map<String, Object>>() {
            @Override
            protected Map<String, Object> process() {
                Map<String, Object> stats = new HashMap<>();
                
                stats.put("totalApplications", applicationRepo.count());
                stats.put("enabledApplications", applicationRepo.countByStatus(ApplicationStatus.ENABLED));
                stats.put("disabledApplications", applicationRepo.countByStatus(ApplicationStatus.DISABLED));
                stats.put("baseApplications", applicationRepo.countByType(ApplicationType.BASE));
                stats.put("businessApplications", applicationRepo.countByType(ApplicationType.BUSINESS));
                stats.put("systemApplications", applicationRepo.countByType(ApplicationType.SYSTEM));
                
                return stats;
            }
        }.execute();
    }
}
