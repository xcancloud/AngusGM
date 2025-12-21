package cloud.xcan.angus.core.gm.application.query.application;

import cloud.xcan.angus.core.gm.domain.application.AppMenu;
import java.util.List;

/**
 * <p>
 * Application Menu Query Service
 * </p>
 */
public interface AppMenuQuery {

  /**
   * Find menu by id and check existence
   */
  AppMenu findAndCheck(Long id);

  /**
   * Find menus by application ID
   */
  List<AppMenu> findByAppId(Long appId);

  /**
   * Check if code exists for application
   */
  boolean existsByAppIdAndCode(Long appId, String code);

  /**
   * Check if code exists excluding specific id
   */
  boolean existsByAppIdAndCodeAndIdNot(Long appId, String code, Long id);

  /**
   * Check if menu has children
   */
  boolean hasChildren(Long menuId);
}

