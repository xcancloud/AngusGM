package cloud.xcan.angus.core.gm.interfaces.application.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.application.AppMenuCmd;
import cloud.xcan.angus.core.gm.application.cmd.application.ApplicationCmd;
import cloud.xcan.angus.core.gm.application.query.application.AppMenuQuery;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.domain.application.AppMenu;
import cloud.xcan.angus.core.gm.domain.application.Application;
import cloud.xcan.angus.core.gm.interfaces.application.facade.ApplicationFacade;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.AppMenuCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.AppMenuSortDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.AppMenuUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationFindDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.internal.assembler.AppMenuAssembler;
import cloud.xcan.angus.core.gm.interfaces.application.facade.internal.assembler.ApplicationAssembler;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.AppMenuVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.AppTagVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationListVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationStatsVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationStatusUpdateVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Application Facade Implementation
 */
@Component
public class ApplicationFacadeImpl implements ApplicationFacade {

  @Resource
  private ApplicationCmd applicationCmd;
  
  @Resource
  private ApplicationQuery applicationQuery;

  @Resource
  private AppMenuCmd appMenuCmd;

  @Resource
  private AppMenuQuery appMenuQuery;

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
    return PageResult.of(page.getTotalElements(), list);
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
    // Verify application exists
    applicationQuery.findById(id);
    
    // Get all menus for the application
    List<AppMenu> menus = appMenuQuery.findByAppId(id);
    
    // Build menu tree
    return AppMenuAssembler.buildMenuTree(menus);
  }

  @Override
  public AppMenuVo createMenu(Long appId, AppMenuCreateDto dto) {
    // Verify application exists
    applicationQuery.findById(appId);
    
    // Convert DTO to Domain
    AppMenu menu = AppMenuAssembler.toEntity(appId, dto);
    
    // Create menu
    AppMenu saved = appMenuCmd.create(menu);
    
    // Convert to VO
    return AppMenuAssembler.toVo(saved);
  }

  @Override
  public AppMenuVo updateMenu(Long appId, Long menuId, AppMenuUpdateDto dto) {
    // Verify application exists
    applicationQuery.findById(appId);
    
    // Get existing menu
    AppMenu existing = appMenuQuery.findAndCheck(menuId);
    if (!existing.getAppId().equals(appId)) {
      throw ResourceNotFound.of("菜单不存在", new Object[]{});
    }
    
    // Convert DTO to Domain
    AppMenu menu = AppMenuAssembler.toEntity(appId, menuId, dto, existing);
    
    // Update menu
    AppMenu saved = appMenuCmd.update(menu);
    
    // Convert to VO
    return AppMenuAssembler.toVo(saved);
  }

  @Override
  public void deleteMenu(Long appId, Long menuId) {
    // Verify application exists
    applicationQuery.findById(appId);
    
    // Verify menu exists and belongs to application
    AppMenu menu = appMenuQuery.findAndCheck(menuId);
    if (!menu.getAppId().equals(appId)) {
      throw ResourceNotFound.of("菜单不存在", new Object[]{});
    }
    
    // Delete menu
    appMenuCmd.delete(menuId);
  }

  @Override
  public void sortMenus(Long appId, AppMenuSortDto dto) {
    // Verify application exists
    applicationQuery.findById(appId);
    
    // Sort menus
    appMenuCmd.sortMenus(appId, dto.getMenuOrders());
  }

  @Override
  public List<AppTagVo> getAvailableTags() {
    // Return predefined available tags according to API documentation
    List<AppTagVo> tags = new ArrayList<>();
    
    tags.add(createTagVo(1L, "HEADER_MENU_POPOVER", "头部菜单自定义下拉菜单"));
    tags.add(createTagVo(2L, "DYNAMIC_POSITION", "标识动态位置显示功能"));
    tags.add(createTagVo(3L, "FIXED_POSITION", "标识固定位置显示功能"));
    tags.add(createTagVo(4L, "CLOUD_SERVICE", "标识云服务应用"));
    tags.add(createTagVo(5L, "ENTERPRISE", "标识企业版应用"));
    tags.add(createTagVo(8L, "DISPLAY_ON_NAVIGATOR", "控制在导航栏展示"));
    tags.add(createTagVo(9L, "DISPLAY_ON_MENU", "控制在菜单栏展示"));
    
    return tags;
  }

  /**
   * <p>
   * Create AppTagVo
   * </p>
   */
  private AppTagVo createTagVo(Long id, String name, String description) {
    AppTagVo vo = new AppTagVo();
    vo.setId(id);
    vo.setName(name);
    vo.setDescription(description);
    return vo;
  }
}
