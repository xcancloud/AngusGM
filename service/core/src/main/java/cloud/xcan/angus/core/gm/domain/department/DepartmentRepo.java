package cloud.xcan.angus.core.gm.domain.department;

import cloud.xcan.angus.core.gm.domain.department.enums.DepartmentStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Department repository interface
 */
@NoRepositoryBean
public interface DepartmentRepo extends BaseRepository<Department, Long> {

  /**
   * Check if code exists
   */
  boolean existsByCode(String code);

  /**
   * Check if code exists excluding specific id
   */
  boolean existsByCodeAndIdNot(String code, Long id);

  /**
   * Find departments by parent id
   */
  List<Department> findByParentId(Long parentId);

  /**
   * Find departments by parent id and status
   */
  List<Department> findByParentIdAndStatus(Long parentId, DepartmentStatus status);

  /**
   * Count departments by status
   */
  long countByStatus(DepartmentStatus status);

  /**
   * Count departments by parent id
   */
  long countByParentId(Long parentId);

  /**
   * Find all root departments (parent_id is null)
   */
  List<Department> findByParentIdIsNull();
}
