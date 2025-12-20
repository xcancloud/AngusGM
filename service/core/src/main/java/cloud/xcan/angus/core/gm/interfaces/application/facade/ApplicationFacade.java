package cloud.xcan.angus.core.gm.interfaces.application.facade;

import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationFindDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationListVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationStatsVo;
import org.springframework.data.domain.Page;

/**
 * Application Facade
 */
public interface ApplicationFacade {

    /**
     * Create application
     */
    ApplicationDetailVo create(ApplicationCreateDto dto);

    /**
     * Update application
     */
    ApplicationDetailVo update(ApplicationUpdateDto dto);

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

    /**
     * Get application detail
     */
    ApplicationDetailVo getDetail(String id);

    /**
     * Find applications
     */
    Page<ApplicationListVo> find(ApplicationFindDto dto);

    /**
     * Get application statistics
     */
    ApplicationStatsVo getStats();
}
