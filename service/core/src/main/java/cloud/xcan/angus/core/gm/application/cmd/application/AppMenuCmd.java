package cloud.xcan.angus.core.gm.application.cmd.application;

import cloud.xcan.angus.core.gm.domain.application.AppMenu;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.AppMenuSortDto;
import java.util.List;

/**
 * <p>
 * Application Menu Command Service
 * </p>
 */
public interface AppMenuCmd {

  /**
   * Create menu
   */
  AppMenu create(AppMenu menu);

  /**
   * Update menu
   */
  AppMenu update(AppMenu menu);

  /**
   * Delete menu
   */
  void delete(Long id);

  /**
   * Sort menus
   */
  void sortMenus(Long appId, List<AppMenuSortDto.MenuOrder> sortItems);
}

