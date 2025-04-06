package cloud.xcan.angus.core.gm.interfaces.app.facade.internal;

import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTargetType;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.tag.WebTagTargetCmd;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagTargetQuery;
import cloud.xcan.angus.core.gm.interfaces.app.facade.AppFuncTagFacade;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.tag.AppTargetTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppTagTargetAssembler;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.tag.AppTagTargetVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class AppFuncTagFacadeImpl implements AppFuncTagFacade {

  @Resource
  private WebTagTargetCmd webTagTargetCmd;

  @Resource
  private WebTagTargetQuery webTagTargetQuery;

  @Override
  public List<IdKey<Long, Object>> funcTagAdd(Long funcId, LinkedHashSet<Long> tagIds) {
    return webTagTargetCmd.appTagAdd(funcId, tagIds);
  }

  @Override
  public void funcTagReplace(Long id, LinkedHashSet<Long> ids) {
    webTagTargetCmd.appTagReplace(id, ids);
  }

  @Override
  public void funcTagDelete(Long funcId, HashSet<Long> tagIds) {
    webTagTargetCmd.appTagDelete(funcId, tagIds);
  }

  @NameJoin
  @Override
  public PageResult<AppTagTargetVo> funcTagList(Long funcId, AppTargetTagFindDto dto) {
    dto.setTargetId(funcId).setTargetType(WebTagTargetType.MENU);
    Page<WebTagTarget> tagTargetsPage = webTagTargetQuery.findTargetTag(
        AppTagTargetAssembler.getTargetTagSpecification(dto), dto.tranPage());
    return buildVoPageResult(tagTargetsPage, AppTagTargetAssembler::toTagTargetVo);
  }
}
