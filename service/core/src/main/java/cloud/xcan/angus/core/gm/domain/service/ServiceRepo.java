package cloud.xcan.angus.core.gm.domain.service;

import cloud.xcan.angus.core.gm.domain.service.enums.ServiceProtocol;
import cloud.xcan.angus.core.gm.domain.service.enums.ServiceStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Service repository interface
 */
@NoRepositoryBean
public interface ServiceRepo extends BaseRepository<Service, Long> {

  /**
   * Check if code exists
   */
  boolean existsByCode(String code);

  /**
   * Check if code exists excluding specific id
   */
  boolean existsByCodeAndIdNot(String code, Long id);

  /**
   * Count services by status
   */
  long countByStatus(ServiceStatus status);

  /**
   * Count services by protocol
   */
  long countByProtocol(ServiceProtocol protocol);

  /**
   * Find services by application id
   */
  List<Service> findByApplicationId(Long applicationId);

  /**
   * Count services by application id
   */
  long countByApplicationId(Long applicationId);
}
