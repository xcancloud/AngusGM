package cloud.xcan.angus.core.gm.domain.interface_;

import cloud.xcan.angus.core.gm.domain.interface_.enums.HttpMethod;
import cloud.xcan.angus.core.gm.domain.interface_.enums.InterfaceStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Interface repository interface
 */
@NoRepositoryBean
public interface InterfaceRepo extends BaseRepository<Interface, Long> {

  /**
   * Check if code exists
   */
  boolean existsByCode(String code);

  /**
   * Check if code exists excluding specific id
   */
  boolean existsByCodeAndIdNot(String code, Long id);

  /**
   * Count interfaces by status
   */
  long countByStatus(InterfaceStatus status);

  /**
   * Count interfaces by method
   */
  long countByMethod(HttpMethod method);

  /**
   * Find interfaces by service id
   */
  List<Interface> findByServiceId(Long serviceId);

  /**
   * Count interfaces by service id
   */
  long countByServiceId(Long serviceId);

  /**
   * Count interfaces requiring authentication
   */
  long countByRequireAuth(Boolean requireAuth);
}
