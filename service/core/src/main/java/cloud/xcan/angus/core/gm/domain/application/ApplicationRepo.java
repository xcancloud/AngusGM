package cloud.xcan.angus.core.gm.domain.application;

import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationStatus;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Application repository interface
 */
@NoRepositoryBean
public interface ApplicationRepo extends BaseRepository<Application, Long> {

  /**
   * Find application by client ID
   */
  Optional<Application> findByClientId(String clientId);

  /**
   * Check if code exists
   */
  boolean existsByCode(String code);

  /**
   * Check if code exists excluding specific id
   */
  boolean existsByCodeAndIdNot(String code, Long id);

  /**
   * Find applications by status
   */
  Page<Application> findByStatus(ApplicationStatus status, Pageable pageable);

  /**
   * Find applications by type
   */
  Page<Application> findByType(ApplicationType type, Pageable pageable);

  /**
   * Find applications by status and type
   */
  Page<Application> findByStatusAndType(ApplicationStatus status, ApplicationType type, Pageable pageable);

  /**
   * Find applications by owner
   */
  Page<Application> findByOwnerId(Long ownerId, Pageable pageable);

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
