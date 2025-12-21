package cloud.xcan.angus.core.gm.domain.application;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * <p>
 * Application Menu repository interface
 * </p>
 */
@NoRepositoryBean
public interface AppMenuRepo extends BaseRepository<AppMenu, Long> {

  /**
   * Find menus by application ID
   */
  List<AppMenu> findByAppId(Long appId);

  /**
   * Find menus by application ID and parent ID
   */
  List<AppMenu> findByAppIdAndParentId(Long appId, Long parentId);

  /**
   * Check if code exists for application
   */
  boolean existsByAppIdAndCode(Long appId, String code);

  /**
   * Check if code exists excluding specific id
   */
  boolean existsByAppIdAndCodeAndIdNot(Long appId, String code, Long id);

  /**
   * Count menus by application ID
   */
  long countByAppId(Long appId);

  /**
   * Count menus by application ID and parent ID
   */
  long countByAppIdAndParentId(Long appId, Long parentId);
}

