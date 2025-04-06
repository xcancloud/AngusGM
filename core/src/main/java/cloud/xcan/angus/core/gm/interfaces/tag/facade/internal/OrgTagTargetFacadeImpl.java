package cloud.xcan.angus.core.gm.interfaces.tag.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.OrgTagAssembler.addDtoToTagTarget;
import static cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.OrgTagTargetAssembler.getTagTargetSpecification;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.tag.OrgTagTargetCmd;
import cloud.xcan.angus.core.gm.application.query.tag.OrgTagTargetQuery;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.OrgTagTargetFacade;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagTargetAddDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagTargetFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.OrgTagAssembler;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.OrgTagTargetDetailVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class OrgTagTargetFacadeImpl implements OrgTagTargetFacade {

  @Resource
  private OrgTagTargetCmd orgTagTargetCmd;

  @Resource
  private OrgTagTargetQuery orgTagTargetQuery;

  @Override
  public List<IdKey<Long, Object>> targetAdd(Long tagId, LinkedHashSet<OrgTagTargetAddDto> dto) {
    List<OrgTagTarget> tagTargets = dto.stream()
        .map(target -> addDtoToTagTarget(tagId, target))
        .collect(Collectors.toList());
    return orgTagTargetCmd.tagTargetAdd(tagId, tagTargets);
  }

  @Override
  public void targetDelete(Long tagId, HashSet<Long> targetIds) {
    orgTagTargetCmd.tagTargetDelete(tagId, targetIds);
  }

  @NameJoin
  @Override
  public PageResult<OrgTagTargetDetailVo> targetList(OrgTagTargetFindDto dto) {
    Page<OrgTagTarget> page = orgTagTargetQuery.findTagTarget(
        getTagTargetSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, OrgTagAssembler::tagTargetToVo);
  }

}
