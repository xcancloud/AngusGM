package cloud.xcan.angus.core.gm.interfaces.group.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.OrgTagTargetAssembler.getTargetTagSpecification;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.tag.OrgTagTargetCmd;
import cloud.xcan.angus.core.gm.application.query.tag.OrgTagTargetQuery;
import cloud.xcan.angus.core.gm.interfaces.group.facade.GroupTagFacade;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTargetTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.OrgTagTargetAssembler;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.OrgTagTargetVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class GroupTagFacadeImpl implements GroupTagFacade {

  @Resource
  private OrgTagTargetCmd orgTagTargetCmd;

  @Resource
  private OrgTagTargetQuery orgTagTargetQuery;

  @Override
  public List<IdKey<Long, Object>> tagAdd(Long groupId, LinkedHashSet<Long> tagIds) {
    return orgTagTargetCmd.groupTagAdd(groupId, tagIds);
  }

  @Override
  public void tagReplace(Long groupId, LinkedHashSet<Long> userIds) {
    orgTagTargetCmd.groupTagReplace(groupId, userIds);
  }

  @Override
  public void tagDelete(Long groupId, HashSet<Long> tagIds) {
    orgTagTargetCmd.groupTagDelete(groupId, tagIds);
  }

  @NameJoin
  @Override
  public PageResult<OrgTagTargetVo> tagList(Long groupId, OrgTargetTagFindDto dto) {
    dto.setTargetId(groupId).setTargetType(OrgTargetType.GROUP);
    Page<OrgTagTarget> tagTargetsPage = orgTagTargetQuery.findTargetTag(
        getTargetTagSpecification(dto), dto.tranPage());
    return buildVoPageResult(tagTargetsPage, OrgTagTargetAssembler::toTagTargetVo);
  }
}
