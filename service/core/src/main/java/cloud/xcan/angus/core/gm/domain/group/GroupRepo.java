package cloud.xcan.angus.core.gm.domain.group;

import cloud.xcan.angus.core.gm.domain.group.enums.GroupStatus;
import cloud.xcan.angus.core.gm.domain.group.enums.GroupType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Group repository interface
 */
@NoRepositoryBean
public interface GroupRepo extends BaseRepository<Group, Long> {

  /**
   * Check if code exists
   */
  boolean existsByCode(String code);

  /**
   * Check if code exists excluding specific id
   */
  boolean existsByCodeAndIdNot(String code, Long id);

  /**
   * Count groups by status
   */
  long countByStatus(GroupStatus status);

  /**
   * Count groups by type
   */
  long countByType(GroupType type);

  /**
   * Count groups by owner
   */
  long countByOwnerId(Long ownerId);
}
