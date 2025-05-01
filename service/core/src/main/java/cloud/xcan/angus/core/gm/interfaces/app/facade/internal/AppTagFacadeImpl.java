package cloud.xcan.angus.core.gm.interfaces.app.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppTagTargetAssembler.getTargetTagSpecification;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTargetType;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.tag.WebTagTargetCmd;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagTargetQuery;
import cloud.xcan.angus.core.gm.interfaces.app.facade.AppTagFacade;
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
public class AppTagFacadeImpl implements AppTagFacade {

  @Resource
  private WebTagTargetCmd webTagTargetCmd;

  @Resource
  private WebTagTargetQuery webTagTargetQuery;

  @Override
  public List<IdKey<Long, Object>> appTagAdd(Long appId, LinkedHashSet<Long> tagIds) {
    return webTagTargetCmd.appTagAdd(appId, tagIds);
  }

  @Override
  public void appTagReplace(Long id, LinkedHashSet<Long> ids) {
    webTagTargetCmd.appTagReplace(id, ids);
  }

  @Override
  public void appTagDelete(Long appId, HashSet<Long> tagIds) {
    webTagTargetCmd.appTagDelete(appId, tagIds);
  }

  @NameJoin
  @Override
  public PageResult<AppTagTargetVo> appTagList(Long appId, AppTargetTagFindDto dto) {
    dto.setTargetId(appId).setTargetType(WebTagTargetType.APP);
    Page<WebTagTarget> tagTargetsPage = webTagTargetQuery.findTargetTag(
        getTargetTagSpecification(dto), dto.tranPage());
    return buildVoPageResult(tagTargetsPage, AppTagTargetAssembler::toTagTargetVo);
  }
}
