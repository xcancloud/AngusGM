package cloud.xcan.angus.core.gm.interfaces.group.facade.internal;

import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;
import static cloud.xcan.angus.core.utils.CoreUtils.getMatchSearchFields;

import cloud.xcan.angus.core.gm.application.cmd.group.GroupCmd;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.domain.group.Group;
import cloud.xcan.angus.core.gm.domain.group.enums.GroupType;
import cloud.xcan.angus.core.gm.interfaces.group.facade.GroupFacade;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupMemberAddDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupMemberFindDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupMemberRemoveDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupOwnerUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.internal.assembler.GroupAssembler;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupListVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupMemberAddVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupMemberVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupOwnerUpdateVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupStatsVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupStatusUpdateVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupUserVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Implementation of group facade
 */
@Service
public class GroupFacadeImpl implements GroupFacade {

  @Resource
  private GroupCmd groupCmd;

  @Resource
  private GroupQuery groupQuery;

  @Override
  public GroupDetailVo create(GroupCreateDto dto) {
    Group group = GroupAssembler.toCreateDomain(dto);
    Group saved = groupCmd.create(group);
    return GroupAssembler.toDetailVo(saved);
  }

  @Override
  public GroupDetailVo update(Long id, GroupUpdateDto dto) {
    Group group = GroupAssembler.toUpdateDomain(id, dto);
    Group saved = groupCmd.update(group);
    return GroupAssembler.toDetailVo(saved);
  }

  @Override
  public GroupStatusUpdateVo updateStatus(Long id, GroupStatusUpdateDto dto) {
    groupCmd.updateStatus(id, dto.getStatus());
    GroupStatusUpdateVo vo = new GroupStatusUpdateVo();
    vo.setId(id);
    vo.setStatus(dto.getStatus());
    vo.setModifiedDate(LocalDateTime.now());
    return vo;
  }

  @Override
  public void delete(Long id) {
    groupCmd.delete(id);
  }

  @Override
  public GroupDetailVo getDetail(Long id) {
    Group group = groupQuery.findAndCheck(id);
    return GroupAssembler.toDetailVo(group);
  }

  @Override
  public PageResult<GroupListVo> list(GroupFindDto dto) {
    GenericSpecification<Group> spec = GroupAssembler.getSpecification(dto);
    Page<Group> page = groupQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, GroupAssembler::toListVo);
  }

  @Override
  public GroupStatsVo getStats() {
    GroupStatsVo stats = new GroupStatsVo();
    
    long totalGroups = groupQuery.count();
    long projectGroups = groupQuery.countByType(GroupType.PROJECT);
    long functionGroups = groupQuery.countByType(GroupType.FUNCTION);
    long tempGroups = groupQuery.countByType(GroupType.TEMP);
    
    stats.setTotalGroups(totalGroups);
    stats.setProjectGroups(projectGroups);
    stats.setFunctionGroups(functionGroups);
    stats.setTempGroups(tempGroups);
    
    // TODO: Calculate active members from user-group relation
    stats.setActiveMembers(0L);
    
    // TODO: Calculate new groups this month
    stats.setNewGroupsThisMonth(0L);
    
    return stats;
  }

  @Override
  public PageResult<GroupMemberVo> listMembers(Long id, GroupMemberFindDto dto) {
    // TODO: Implement member listing with pagination
    return PageResult.empty();
  }

  @Override
  public GroupMemberAddVo addMembers(Long id, GroupMemberAddDto dto) {
    groupCmd.addMembers(id, dto.getUserIds());
    GroupMemberAddVo vo = new GroupMemberAddVo();
    vo.setGroupId(id);
    vo.setAddedCount(dto.getUserIds().size());
    vo.setAddedUsers(new ArrayList<>());
    return vo;
  }

  @Override
  public void removeMember(Long id, Long userId) {
    groupCmd.removeMember(id, userId);
  }

  @Override
  public void removeMembers(Long id, GroupMemberRemoveDto dto) {
    groupCmd.removeMembers(id, dto.getUserIds());
  }

  @Override
  public GroupOwnerUpdateVo updateOwner(Long id, GroupOwnerUpdateDto dto) {
    groupCmd.updateOwner(id, dto.getOwnerId());
    GroupOwnerUpdateVo vo = new GroupOwnerUpdateVo();
    vo.setGroupId(id);
    vo.setOwnerId(dto.getOwnerId());
    vo.setModifiedDate(LocalDateTime.now());
    // TODO: Set owner name from user query
    return vo;
  }

  @Override
  public List<GroupUserVo> getGroupsByUser(Long userId) {
    List<Group> groups = groupQuery.findByUserId(userId);
    return GroupAssembler.toUserVoList(groups);
  }
}
