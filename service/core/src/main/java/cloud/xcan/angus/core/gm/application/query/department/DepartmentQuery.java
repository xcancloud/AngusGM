package cloud.xcan.angus.core.gm.application.query.department;

import cloud.xcan.angus.core.gm.domain.department.Department;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Department query service interface
 */
public interface DepartmentQuery {

  /**
   * Find department by id and check existence
   */
  Department findAndCheck(Long id);

  /**
   * Find departments with pagination
   */
  Page<Department> find(GenericSpecification<Department> spec, PageRequest pageable,
                        boolean fullTextSearch, String[] match);

  /**
   * Find department tree structure
   */
  List<Department> findTree(Long parentId, String status);

  /**
   * Check if code exists
   */
  boolean existsByCode(String code);

  /**
   * Get department statistics
   */
  DepartmentStatsVo getStats();
}
