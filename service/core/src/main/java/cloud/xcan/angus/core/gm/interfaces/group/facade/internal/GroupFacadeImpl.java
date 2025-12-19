package cloud.xcan.angus.core.gm.interfaces.group.facade.internal;

import static cloud.xcan.angus.spec.BizConstant.buildVoPageResult;
import static cloud.xcan.angus.spec.BizConstant.getMatchSearchFields;

import cloud.xcan.angus.core.gm.application.cmd.group.GroupCmd;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.domain.group.Group;
import cloud.xcan.angus.core.gm.domain.group.GroupRepo;
import cloud.xcan.angus.core.gm.domain.group.enums.GroupStatus;
import cloud.xcan.angus.core.gm.domain.group.enums.GroupType;
import cloud.xcan.angus.core.gm.interfaces.group.facade.GroupFacade;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.internal.assembler.GroupAssembler;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupListVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
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

  @Resource
  private GroupRepo groupRepo;

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
  public void enable(Long id) {
    groupCmd.enable(id);
  }

  @Override
  public void disable(Long id) {
    groupCmd.disable(id);
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
    
    long totalGroups = groupRepo.count();
    long enabledGroups = groupRepo.countByStatus(GroupStatus.ENABLED);
    long disabledGroups = groupRepo.countByStatus(GroupStatus.DISABLED);
    long systemGroups = groupRepo.countByType(GroupType.SYSTEM);
    long customGroups = groupRepo.countByType(GroupType.CUSTOM);
    
    stats.setTotalGroups(totalGroups);
    stats.setEnabledGroups(enabledGroups);
    stats.setDisabledGroups(disabledGroups);
    stats.setSystemGroups(systemGroups);
    stats.setCustomGroups(customGroups);
    
    // TODO: Calculate average member count
    stats.setAverageMemberCount(0.0);
    
    return stats;
  }
}
