package cloud.xcan.angus.core.gm.interfaces.application.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.application.ApplicationCmd;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.domain.application.Application;
import cloud.xcan.angus.core.gm.interfaces.application.facade.ApplicationFacade;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.AppMenuCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.AppMenuSortDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.AppMenuUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationFindDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.internal.assembler.ApplicationAssembler;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.AppMenuVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.AppTagVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationListVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationStatsVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationStatusUpdateVo;
import cloud.xcan.angus.remote.PageResult;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

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
  public ApplicationDetailVo update(Long id, ApplicationUpdateDto dto) {
    Application existing = applicationQuery.findById(id);
    Application application = ApplicationAssembler.toEntity(dto, existing);
    application = applicationCmd.update(application);
    return ApplicationAssembler.toDetailVo(application);
  }

  @Override
  public ApplicationStatusUpdateVo updateStatus(Long id, ApplicationStatusUpdateDto dto) {
    applicationCmd.updateStatus(id, dto.getStatus());
    ApplicationStatusUpdateVo vo = new ApplicationStatusUpdateVo();
    vo.setId(id);
    vo.setStatus(dto.getStatus());
    vo.setModifiedDate(LocalDateTime.now());
    return vo;
  }

  @Override
  public void delete(Long id) {
    applicationCmd.delete(id);
  }

  @Override
  public ApplicationDetailVo getDetail(Long id) {
    Application application = applicationQuery.findById(id);
    return ApplicationAssembler.toDetailVo(application);
  }

  @Override
  public PageResult<ApplicationListVo> find(ApplicationFindDto dto) {
    Page<Application> page = applicationQuery.find(dto);
    List<ApplicationListVo> list = page.getContent().stream()
        .map(ApplicationAssembler::toListVo)
        .toList();
    return PageResult.of(list, page.getTotalElements());
  }

  @Override
  public ApplicationStatsVo getStats() {
    Map<String, Object> stats = applicationQuery.getStats();
    ApplicationStatsVo vo = new ApplicationStatsVo();
    vo.setTotalApplications((Long) stats.getOrDefault("totalApplications", 0L));
    vo.setEnabledApplications((Long) stats.getOrDefault("enabledApplications", 0L));
    vo.setDisabledApplications((Long) stats.getOrDefault("disabledApplications", 0L));
    vo.setBaseApplications((Long) stats.getOrDefault("baseApplications", 0L));
    vo.setBusinessApplications((Long) stats.getOrDefault("businessApplications", 0L));
    vo.setSystemApplications((Long) stats.getOrDefault("systemApplications", 0L));
    return vo;
  }

  @Override
  public List<AppMenuVo> getMenus(Long id) {
    // TODO: Implement menu tree retrieval
    return new ArrayList<>();
  }

  @Override
  public AppMenuVo createMenu(Long appId, AppMenuCreateDto dto) {
    // TODO: Implement menu creation
    return new AppMenuVo();
  }

  @Override
  public AppMenuVo updateMenu(Long appId, Long menuId, AppMenuUpdateDto dto) {
    // TODO: Implement menu update
    return new AppMenuVo();
  }

  @Override
  public void deleteMenu(Long appId, Long menuId) {
    // TODO: Implement menu deletion
  }

  @Override
  public void sortMenus(Long appId, AppMenuSortDto dto) {
    // TODO: Implement menu sorting
  }

  @Override
  public List<AppTagVo> getAvailableTags() {
    // TODO: Implement available tags retrieval
    return new ArrayList<>();
  }
}
