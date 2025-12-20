package cloud.xcan.angus.core.gm.domain.application;

import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationStatus;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Application repository interface
 */
@NoRepositoryBean
public interface ApplicationRepo extends BaseRepository<Application, Long> {

  /**
   * Check if code exists
   */
  boolean existsByCode(String code);

  /**
   * Check if code exists excluding specific id
   */
  boolean existsByCodeAndIdNot(String code, Long id);

  /**
   * Count applications by status
   */
  long countByStatus(ApplicationStatus status);

  /**
   * Count applications by type
   */
  long countByType(ApplicationType type);

  /**
   * Count applications by owner
   */
  long countByOwnerId(Long ownerId);
}
