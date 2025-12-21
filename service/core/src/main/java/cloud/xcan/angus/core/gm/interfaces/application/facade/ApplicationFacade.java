package cloud.xcan.angus.core.gm.interfaces.application.facade;

import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.AppMenuCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.AppMenuSortDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.AppMenuUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationFindDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.AppMenuVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.AppTagVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationListVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationStatsVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationStatusUpdateVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

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
  ApplicationDetailVo update(Long id, ApplicationUpdateDto dto);

  /**
   * Update application status (enable/disable)
   */
  ApplicationStatusUpdateVo updateStatus(Long id, ApplicationStatusUpdateDto dto);

  /**
   * Delete application
   */
  void delete(Long id);

  /**
   * Get application detail
   */
  ApplicationDetailVo getDetail(Long id);

  /**
   * Find applications with pagination
   */
  PageResult<ApplicationListVo> find(ApplicationFindDto dto);

  /**
   * Get application statistics
   */
  ApplicationStatsVo getStats();

  /**
   * Get application menus tree
   */
  List<AppMenuVo> getMenus(Long id);

  /**
   * Create application menu
   */
  AppMenuVo createMenu(Long appId, AppMenuCreateDto dto);

  /**
   * Update application menu
   */
  AppMenuVo updateMenu(Long appId, Long menuId, AppMenuUpdateDto dto);

  /**
   * Delete application menu
   */
  void deleteMenu(Long appId, Long menuId);

  /**
   * Sort application menus
   */
  void sortMenus(Long appId, AppMenuSortDto dto);

  /**
   * Get available tags
   */
  List<AppTagVo> getAvailableTags();
}
