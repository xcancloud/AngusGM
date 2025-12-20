package cloud.xcan.angus.core.gm.interfaces.application;

import cloud.xcan.angus.core.gm.interfaces.application.facade.ApplicationFacade;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationFindDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationListVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationStatsVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * Application REST Controller
 */
@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationRest {

    private final ApplicationFacade applicationFacade;

    /**
     * Create application
     */
    @PostMapping
    public ApplicationDetailVo create(@Valid @RequestBody ApplicationCreateDto dto) {
        return applicationFacade.create(dto);
    }

    /**
     * Update application
     */
    @PatchMapping
    public ApplicationDetailVo update(@Valid @RequestBody ApplicationUpdateDto dto) {
        return applicationFacade.update(dto);
    }

    /**
     * Enable application
     */
    @PostMapping("/{id}/enable")
    public void enable(@PathVariable String id) {
        applicationFacade.enable(id);
    }

    /**
     * Disable application
     */
    @PostMapping("/{id}/disable")
    public void disable(@PathVariable String id) {
        applicationFacade.disable(id);
    }

    /**
     * Delete application
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        applicationFacade.delete(id);
    }

    /**
     * Get application detail
     */
    @GetMapping("/{id}")
    public ApplicationDetailVo getDetail(@PathVariable String id) {
        return applicationFacade.getDetail(id);
    }

    /**
     * Find applications
     */
    @GetMapping
    public Page<ApplicationListVo> find(ApplicationFindDto dto) {
        return applicationFacade.find(dto);
    }

    /**
     * Get application statistics
     */
    @GetMapping("/stats")
    public ApplicationStatsVo getStats() {
        return applicationFacade.getStats();
    }
}
