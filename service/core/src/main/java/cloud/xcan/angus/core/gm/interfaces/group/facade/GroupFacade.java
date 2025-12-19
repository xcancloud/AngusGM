package cloud.xcan.angus.core.gm.interfaces.group.facade;

import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupListVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupStatsVo;
import cloud.xcan.angus.remote.PageResult;

/**
 * Group facade interface
 */
public interface GroupFacade {

  /**
   * Create group
   */
  GroupDetailVo create(GroupCreateDto dto);

  /**
   * Update group
   */
  GroupDetailVo update(Long id, GroupUpdateDto dto);

  /**
   * Enable group
   */
  void enable(Long id);

  /**
   * Disable group
   */
  void disable(Long id);

  /**
   * Delete group
   */
  void delete(Long id);

  /**
   * Get group detail
   */
  GroupDetailVo getDetail(Long id);

  /**
   * List groups with pagination
   */
  PageResult<GroupListVo> list(GroupFindDto dto);

  /**
   * Get group statistics
   */
  GroupStatsVo getStats();
}
