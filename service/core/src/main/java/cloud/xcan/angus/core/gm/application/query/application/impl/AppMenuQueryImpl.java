package cloud.xcan.angus.core.gm.application.query.application.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.application.AppMenuQuery;
import cloud.xcan.angus.core.gm.domain.application.AppMenu;
import cloud.xcan.angus.core.gm.domain.application.AppMenuRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Application Menu Query Service Implementation
 * </p>
 */
@Biz
public class AppMenuQueryImpl implements AppMenuQuery {

  @Resource
  private AppMenuRepo appMenuRepo;

  @Override
  public AppMenu findAndCheck(Long id) {
    return new BizTemplate<AppMenu>() {
      @Override
      protected AppMenu process() {
        return appMenuRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("菜单不存在", new Object[]{}));
      }
    }.execute();
  }

  @Override
  public List<AppMenu> findByAppId(Long appId) {
    return new BizTemplate<List<AppMenu>>() {
      @Override
      protected List<AppMenu> process() {
        return appMenuRepo.findByAppId(appId);
      }
    }.execute();
  }

  @Override
  public boolean existsByAppIdAndCode(Long appId, String code) {
    return appMenuRepo.existsByAppIdAndCode(appId, code);
  }

  @Override
  public boolean existsByAppIdAndCodeAndIdNot(Long appId, String code, Long id) {
    return appMenuRepo.existsByAppIdAndCodeAndIdNot(appId, code, id);
  }

  @Override
  public boolean hasChildren(Long menuId) {
    return new BizTemplate<Boolean>() {
      @Override
      protected Boolean process() {
        // Find the menu to get its appId
        AppMenu menu = appMenuRepo.findById(menuId).orElse(null);
        if (menu == null) {
          return false;
        }
        List<AppMenu> children = appMenuRepo.findByAppIdAndParentId(menu.getAppId(), menuId);
        return children != null && !children.isEmpty();
      }
    }.execute();
  }
}

