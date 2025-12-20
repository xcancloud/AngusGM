package cloud.xcan.angus.core.gm.interfaces.application.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.application.ApplicationCmd;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.domain.application.Application;
import cloud.xcan.angus.core.gm.interfaces.application.facade.ApplicationFacade;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationFindDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.internal.assembler.ApplicationAssembler;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationListVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationStatsVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Application Facade Implementation
 */
@Service
@RequiredArgsConstructor
public class ApplicationFacadeImpl implements ApplicationFacade {

    private final ApplicationCmd applicationCmd;
    private final ApplicationQuery applicationQuery;

    @Override
    public ApplicationDetailVo create(ApplicationCreateDto dto) {
        Application application = ApplicationAssembler.toEntity(dto);
        application = applicationCmd.create(application);
        return ApplicationAssembler.toDetailVo(application);
    }

    @Override
    public ApplicationDetailVo update(ApplicationUpdateDto dto) {
        Application existing = applicationQuery.findById(dto.getId());
        Application application = ApplicationAssembler.toEntity(dto, existing);
        application = applicationCmd.update(application);
        return ApplicationAssembler.toDetailVo(application);
    }

    @Override
    public void enable(String id) {
        applicationCmd.enable(id);
    }

    @Override
    public void disable(String id) {
        applicationCmd.disable(id);
    }

    @Override
    public void delete(String id) {
        applicationCmd.delete(id);
    }

    @Override
    public ApplicationDetailVo getDetail(String id) {
        Application application = applicationQuery.findById(id);
        return ApplicationAssembler.toDetailVo(application);
    }

    @Override
    public Page<ApplicationListVo> find(ApplicationFindDto dto) {
        Page<Application> page = applicationQuery.find(dto);
        return page.map(ApplicationAssembler::toListVo);
    }

    @Override
    public ApplicationStatsVo getStats() {
        Map<String, Object> stats = applicationQuery.getStats();
        ApplicationStatsVo vo = new ApplicationStatsVo();
        vo.setTotalApplications((Long) stats.get("totalApplications"));
        vo.setEnabledApplications((Long) stats.get("enabledApplications"));
        vo.setDisabledApplications((Long) stats.get("disabledApplications"));
        vo.setWebApplications((Long) stats.get("webApplications"));
        vo.setMobileApplications((Long) stats.get("mobileApplications"));
        vo.setDesktopApplications((Long) stats.get("desktopApplications"));
        vo.setApiApplications((Long) stats.get("apiApplications"));
        return vo;
    }
}
