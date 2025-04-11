package cloud.xcan.angus.core.gm.interfaces.tag.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.WebTagTargetAssembler.addDtoToTagTarget;
import static cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.WebTagTargetAssembler.getTagTargetSpecification;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.tag.WebTagTargetCmd;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagTargetQuery;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.WebTagTargetFacade;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagTargetAddDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagTargetFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.WebTagTargetAssembler;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.WebTagTargetDetailVo;
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
public class WebTagTargetFacadeImpl implements WebTagTargetFacade {

  @Resource
  private WebTagTargetCmd webTagTargetCmd;

  @Resource
  private WebTagTargetQuery webTagTargetQuery;

  @Override
  public List<IdKey<Long, Object>> targetAdd(Long tagId, LinkedHashSet<WebTagTargetAddDto> dto) {
    List<WebTagTarget> tagTargets = dto.stream()
        .map(target -> addDtoToTagTarget(tagId, target))
        .collect(Collectors.toList());
    return webTagTargetCmd.tagTargetAdd(tagId, tagTargets);
  }

  @Override
  public void targetDelete(Long tagId, HashSet<Long> targetIds) {
    webTagTargetCmd.tagTargetDelete(tagId, targetIds);
  }

  @NameJoin
  @Override
  public PageResult<WebTagTargetDetailVo> targetList(WebTagTargetFindDto dto) {
    Page<WebTagTarget> page = webTagTargetQuery.findTagTarget(
        getTagTargetSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, WebTagTargetAssembler::tagTargetToDetailVo);
  }

}
