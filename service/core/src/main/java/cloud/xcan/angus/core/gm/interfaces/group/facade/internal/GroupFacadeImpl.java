package cloud.xcan.angus.core.gm.interfaces.group.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.group.facade.internal.assembler.GroupAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.group.facade.internal.assembler.GroupAssembler.toDetailVo;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.gm.group.dto.GroupFindDto;
import cloud.xcan.angus.api.gm.group.vo.GroupDetailVo;
import cloud.xcan.angus.api.gm.group.vo.GroupListVo;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.group.GroupCmd;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.interfaces.group.facade.GroupFacade;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupAddDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.internal.assembler.GroupAssembler;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class GroupFacadeImpl implements GroupFacade {

  @Resource
  private GroupCmd groupCmd;

  @Resource
  private GroupQuery groupQuery;

  @Override
  public List<IdKey<Long, Object>> add(List<GroupAddDto> dto) {
    List<Group> groups = dto.stream().map(GroupAssembler::addDtoToDomain)
        .collect(Collectors.toList());
    return groupCmd.add(groups);
  }

  @Override
  public void update(List<GroupUpdateDto> dto) {
    List<Group> groups = dto.stream().map(GroupAssembler::updateDtoToDomain)
        .collect(Collectors.toList());
    groupCmd.update(groups);
  }

  @Override
  public List<IdKey<Long, Object>> replace(List<GroupReplaceDto> dto) {
    List<Group> groups = dto.stream().map(GroupAssembler::replaceDtoToDomain)
        .collect(Collectors.toList());
    return groupCmd.replace(groups);
  }

  @Override
  public void delete(HashSet<Long> ids) {
    groupCmd.delete(ids);
  }

  @Override
  public void enabled(Set<EnabledOrDisabledDto> dto) {
    List<Group> groups = dto.stream().map(GroupAssembler::enabledDtoToDomain)
        .collect(Collectors.toList());
    groupCmd.enabled(groups);
  }

  @NameJoin
  @Override
  public GroupDetailVo detail(Long id) {
    return toDetailVo(groupQuery.detail(id));
  }

  @NameJoin
  @Override
  public PageResult<GroupListVo> list(GroupFindDto dto) {
    Page<Group> page = groupQuery.list(getSpecification(dto), dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, GroupAssembler::toListVo);
  }

}
