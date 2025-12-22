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

  /**
   * Update department manager
   */
  Department updateManager(Long id, Long managerId);

  /**
   * Add users to department
   */
  int addMembers(Long departmentId, java.util.List<Long> userIds);

  /**
   * Remove user from department
   */
  void removeMember(Long departmentId, Long userId);

  /**
   * Batch remove users from department
   */
  void removeMembers(Long departmentId, java.util.List<Long> userIds);

  /**
   * Transfer users from one department to another
   */
  int transferMembers(Long sourceDepartmentId, Long targetDepartmentId, java.util.List<Long> userIds);
}
