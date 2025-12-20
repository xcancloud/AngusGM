package cloud.xcan.angus.core.gm.domain.user;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * User repository interface
 */
@NoRepositoryBean
public interface UserRepo extends BaseRepository<User, Long> {

  /**
   * Check if username exists
   */
  boolean existsByUsername(String username);

  /**
   * Check if username exists excluding specific id
   */
  boolean existsByUsernameAndIdNot(String username, Long id);

  /**
   * Check if email exists
   */
  boolean existsByEmail(String email);

  /**
   * Find user by username
   */
  User findByUsername(String username);

  /**
   * Count users by department
   */
  long countByDepartmentId(Long departmentId);

  /**
   * Count users by status
   */
  long countByStatus(cloud.xcan.angus.core.gm.domain.user.enums.UserStatus status);

  /**
   * Count users by enable status
   */
  long countByEnableStatus(cloud.xcan.angus.core.gm.domain.user.enums.EnableStatus enableStatus);
}
