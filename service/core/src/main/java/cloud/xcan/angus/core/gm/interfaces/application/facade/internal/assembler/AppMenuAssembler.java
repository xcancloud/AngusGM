package cloud.xcan.angus.core.gm.interfaces.application.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.application.AppMenu;
import cloud.xcan.angus.core.gm.domain.application.enums.AppMenuType;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.AppMenuCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.AppMenuUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.AppMenuVo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * Application Menu Assembler
 * </p>
 */
public class AppMenuAssembler {

  /**
   * <p>
   * Convert CreateDto to Domain
   * </p>
   */
  public static AppMenu toEntity(Long appId, AppMenuCreateDto dto) {
    AppMenu menu = new AppMenu();
    menu.setAppId(appId);
    menu.setName(dto.getName());
    menu.setCode(dto.getCode());
    menu.setIcon(dto.getIcon());
    menu.setPath(dto.getPath());
    menu.setParentId(dto.getParentId());
    menu.setSortOrder(dto.getSortOrder());
    menu.setIsVisible(dto.getIsVisible());
    menu.setPermission(dto.getPermission());
    menu.setType(AppMenuType.MENU);
    return menu;
  }

  /**
   * <p>
   * Convert UpdateDto to Domain
   * </p>
   */
  public static AppMenu toEntity(Long appId, Long menuId, AppMenuUpdateDto dto, AppMenu existing) {
    if (dto.getName() != null) {
      existing.setName(dto.getName());
    }
    if (dto.getIcon() != null) {
      existing.setIcon(dto.getIcon());
    }
    if (dto.getPath() != null) {
      existing.setPath(dto.getPath());
    }
    if (dto.getSortOrder() != null) {
      existing.setSortOrder(dto.getSortOrder());
    }
    if (dto.getIsVisible() != null) {
      existing.setIsVisible(dto.getIsVisible());
    }
    if (dto.getPermission() != null) {
      existing.setPermission(dto.getPermission());
    }
    return existing;
  }

  /**
   * <p>
   * Convert Domain to VO
   * </p>
   */
  public static AppMenuVo toVo(AppMenu menu) {
    AppMenuVo vo = new AppMenuVo();
    vo.setId(menu.getId());
    vo.setAppId(menu.getAppId());
    vo.setName(menu.getName());
    vo.setCode(menu.getCode());
    vo.setIcon(menu.getIcon());
    vo.setPath(menu.getPath());
    vo.setParentId(menu.getParentId());
    vo.setSortOrder(menu.getSortOrder());
    vo.setIsVisible(menu.getIsVisible());
    vo.setPermission(menu.getPermission());
    
    // Set auditing fields
    vo.setTenantId(menu.getTenantId());
    vo.setCreatedBy(menu.getCreatedBy());
    vo.setCreatedDate(menu.getCreatedDate());
    if (menu.getModifiedBy() != null) {
      vo.setModifiedBy(menu.getModifiedBy());
    }
    if (menu.getModifiedDate() != null) {
      vo.setModifiedDate(menu.getModifiedDate());
    }
    
    return vo;
  }

  /**
   * <p>
   * Build menu tree from flat list
   * </p>
   */
  public static List<AppMenuVo> buildMenuTree(List<AppMenu> menus) {
    if (menus == null || menus.isEmpty()) {
      return new ArrayList<>();
    }

    // Convert to VO list
    List<AppMenuVo> voList = menus.stream()
        .map(AppMenuAssembler::toVo)
        .collect(Collectors.toList());

    // Build tree structure
    Map<Long, AppMenuVo> voMap = voList.stream()
        .collect(Collectors.toMap(AppMenuVo::getId, vo -> vo));

    List<AppMenuVo> rootMenus = new ArrayList<>();
    for (AppMenuVo vo : voList) {
      if (vo.getParentId() == null || vo.getParentId() <= 0) {
        rootMenus.add(vo);
      } else {
        AppMenuVo parent = voMap.get(vo.getParentId());
        if (parent != null) {
          if (parent.getChildren() == null) {
            parent.setChildren(new ArrayList<>());
          }
          parent.getChildren().add(vo);
        }
      }
    }

    // Sort menus by sortOrder
    rootMenus.sort((a, b) -> {
      int orderA = a.getSortOrder() != null ? a.getSortOrder() : 0;
      int orderB = b.getSortOrder() != null ? b.getSortOrder() : 0;
      return Integer.compare(orderA, orderB);
    });

    // Sort children recursively
    sortMenuChildren(rootMenus);

    return rootMenus;
  }

  /**
   * <p>
   * Sort menu children recursively
   * </p>
   */
  private static void sortMenuChildren(List<AppMenuVo> menus) {
    if (menus == null || menus.isEmpty()) {
      return;
    }
    menus.sort((a, b) -> {
      int orderA = a.getSortOrder() != null ? a.getSortOrder() : 0;
      int orderB = b.getSortOrder() != null ? b.getSortOrder() : 0;
      return Integer.compare(orderA, orderB);
    });
    for (AppMenuVo menu : menus) {
      if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
        sortMenuChildren(menu.getChildren());
      }
    }
  }
}

