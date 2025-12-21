package cloud.xcan.angus.core.gm.application.cmd.application.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.application.AppMenuCmd;
import cloud.xcan.angus.core.gm.application.query.application.AppMenuQuery;
import cloud.xcan.angus.core.gm.domain.application.AppMenu;
import cloud.xcan.angus.core.gm.domain.application.AppMenuRepo;
import cloud.xcan.angus.core.gm.domain.application.enums.AppMenuType;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.AppMenuSortDto;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Application Menu Command Service Implementation
 * </p>
 */
@Biz
public class AppMenuCmdImpl implements AppMenuCmd {

  @Resource
  private AppMenuRepo appMenuRepo;

  @Resource
  private AppMenuQuery appMenuQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AppMenu create(AppMenu menu) {
    return new BizTemplate<AppMenu>() {
      @Override
      protected void checkParams() {
        if (appMenuQuery.existsByAppIdAndCode(menu.getAppId(), menu.getCode())) {
          throw ResourceExisted.of("菜单编码「{0}」已存在", new Object[]{menu.getCode()});
        }
        if (menu.getParentId() != null && menu.getParentId() > 0) {
          appMenuQuery.findAndCheck(menu.getParentId());
        }
      }

      @Override
      protected AppMenu process() {
        if (menu.getType() == null) {
          menu.setType(AppMenuType.MENU);
        }
        if (menu.getIsVisible() == null) {
          menu.setIsVisible(true);
        }
        if (menu.getSortOrder() == null) {
          menu.setSortOrder(0);
        }
        if (menu.getParentId() != null && menu.getParentId() <= 0) {
          menu.setParentId(null);
        }
        return appMenuRepo.save(menu);
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AppMenu update(AppMenu menu) {
    return new BizTemplate<AppMenu>() {
      AppMenu menuDb;

      @Override
      protected void checkParams() {
        menuDb = appMenuQuery.findAndCheck(menu.getId());
        if (!menuDb.getAppId().equals(menu.getAppId())) {
          throw ResourceNotFound.of("菜单不存在", new Object[]{});
        }
        if (menu.getCode() != null && !menu.getCode().equals(menuDb.getCode())) {
          if (appMenuQuery.existsByAppIdAndCodeAndIdNot(menu.getAppId(), menu.getCode(), menu.getId())) {
            throw ResourceExisted.of("菜单编码「{0}」已存在", new Object[]{menu.getCode()});
          }
        }
        if (menu.getParentId() != null && menu.getParentId() > 0 && !menu.getParentId().equals(menu.getId())) {
          appMenuQuery.findAndCheck(menu.getParentId());
        }
      }

      @Override
      protected AppMenu process() {
        if (menu.getCode() != null) {
          menuDb.setCode(menu.getCode());
        }
        if (menu.getName() != null) {
          menuDb.setName(menu.getName());
        }
        if (menu.getShowName() != null) {
          menuDb.setShowName(menu.getShowName());
        }
        if (menu.getIcon() != null) {
          menuDb.setIcon(menu.getIcon());
        }
        if (menu.getPath() != null) {
          menuDb.setPath(menu.getPath());
        }
        if (menu.getSortOrder() != null) {
          menuDb.setSortOrder(menu.getSortOrder());
        }
        if (menu.getIsVisible() != null) {
          menuDb.setIsVisible(menu.getIsVisible());
        }
        if (menu.getDescription() != null) {
          menuDb.setDescription(menu.getDescription());
        }
        if (menu.getPermission() != null) {
          menuDb.setPermission(menu.getPermission());
        }
        if (menu.getParentId() != null) {
          if (menu.getParentId() <= 0) {
            menuDb.setParentId(null);
          } else {
            menuDb.setParentId(menu.getParentId());
          }
        }
        return appMenuRepo.save(menuDb);
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        AppMenu menu = appMenuQuery.findAndCheck(id);
        if (appMenuQuery.hasChildren(id)) {
          throw ResourceExisted.of("菜单下存在子菜单，无法删除", new Object[]{});
        }
      }

      @Override
      protected Void process() {
        appMenuRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void sortMenus(Long appId, List<AppMenuSortDto.MenuOrder> sortItems) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        for (AppMenuSortDto.MenuOrder item : sortItems) {
          AppMenu menu = appMenuQuery.findAndCheck(item.getId());
          if (!menu.getAppId().equals(appId)) {
            continue;
          }
          menu.setSortOrder(item.getSortOrder());
          appMenuRepo.save(menu);
        }
        return null;
      }
    }.execute();
  }
}

