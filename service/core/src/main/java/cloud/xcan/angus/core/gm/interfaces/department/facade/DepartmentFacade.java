package cloud.xcan.angus.core.gm.interfaces.department.facade;

import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentCreateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentFindDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentListVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentStatsVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

/**
 * Department facade interface
 */
public interface DepartmentFacade {

  /**
   * Create department
   */
  DepartmentDetailVo create(DepartmentCreateDto dto);

  /**
   * Update department
   */
  DepartmentDetailVo update(Long id, DepartmentUpdateDto dto);

  /**
   * Enable department
   */
  void enable(Long id);

  /**
   * Disable department
   */
  void disable(Long id);

  /**
   * Delete department
   */
  void delete(Long id);

  /**
   * Get department detail
   */
  DepartmentDetailVo getDetail(Long id);

  /**
   * List departments with pagination
   */
  PageResult<DepartmentListVo> list(DepartmentFindDto dto);

  /**
   * Get department statistics
   */
  DepartmentStatsVo getStats();

  /**
   * Get department tree structure
   */
  List<DepartmentDetailVo> getTree(Long parentId);
}
