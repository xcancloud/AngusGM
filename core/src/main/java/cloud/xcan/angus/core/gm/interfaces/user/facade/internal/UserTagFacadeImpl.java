package cloud.xcan.angus.core.gm.interfaces.user.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.OrgTagTargetAssembler.getTargetTagSpecification;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.tag.OrgTagTargetCmd;
import cloud.xcan.angus.core.gm.application.query.tag.OrgTagTargetQuery;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTargetTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.OrgTagTargetAssembler;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.OrgTagTargetVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserTagFacade;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserTagFacadeImpl implements UserTagFacade {

  @Resource
  private OrgTagTargetCmd orgTagTargetCmd;

  @Resource
  private OrgTagTargetQuery orgTagTargetQuery;

  @Override
  public List<IdKey<Long, Object>> tagAdd(Long userId, LinkedHashSet<Long> tagIds) {
    return orgTagTargetCmd.userTagAdd(userId, tagIds);
  }

  @Override
  public void tagReplace(Long userId, LinkedHashSet<Long> tagIds) {
    orgTagTargetCmd.userTagReplace(userId, tagIds);
  }

  @Override
  public void tagDelete(Long userId, HashSet<Long> tagIds) {
    orgTagTargetCmd.userTagDelete(userId, tagIds);
  }

  @NameJoin
  @Override
  public PageResult<OrgTagTargetVo> tagList(Long userId, OrgTargetTagFindDto dto) {
    dto.setTargetId(userId).setTargetType(OrgTargetType.USER);
    Page<OrgTagTarget> page = orgTagTargetQuery.findTargetTag(
        getTargetTagSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, OrgTagTargetAssembler::toTagTargetVo);
  }

}
