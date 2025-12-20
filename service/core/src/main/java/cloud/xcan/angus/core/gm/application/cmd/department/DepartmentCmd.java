package cloud.xcan.angus.core.gm.application.cmd.department;

import cloud.xcan.angus.core.gm.domain.department.Department;

/**
 * Department command service interface
 */
public interface DepartmentCmd {

  /**
   * Create department
   */
  Department create(Department department);

  /**
   * Update department
   */
  Department update(Department department);

  /**
   * Delete department
   */
  void delete(Long id);

  /**
   * Enable department
   */
  void enable(Long id);

  /**
   * Disable department
   */
  void disable(Long id);
}
