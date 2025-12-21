package cloud.xcan.angus.core.gm.interfaces.group.facade;

import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupMemberAddDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupMemberFindDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupMemberRemoveDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupOwnerUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupListVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupMemberAddVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupMemberVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupOwnerUpdateVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupStatsVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupStatusUpdateVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupUserVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

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
   * Update group status (archive/activate)
   */
  GroupStatusUpdateVo updateStatus(Long id, GroupStatusUpdateDto dto);

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

  /**
   * List group members with pagination
   */
  PageResult<GroupMemberVo> listMembers(Long id, GroupMemberFindDto dto);

  /**
   * Add members to group
   */
  GroupMemberAddVo addMembers(Long id, GroupMemberAddDto dto);

  /**
   * Remove member from group
   */
  void removeMember(Long id, Long userId);

  /**
   * Remove multiple members from group
   */
  void removeMembers(Long id, GroupMemberRemoveDto dto);

  /**
   * Update group owner
   */
  GroupOwnerUpdateVo updateOwner(Long id, GroupOwnerUpdateDto dto);

  /**
   * Get groups by user
   */
  List<GroupUserVo> getGroupsByUser(Long userId);
}
