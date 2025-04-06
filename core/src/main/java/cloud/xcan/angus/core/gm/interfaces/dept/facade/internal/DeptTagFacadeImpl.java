package cloud.xcan.angus.core.gm.interfaces.dept.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.OrgTagTargetAssembler.getTargetTagSpecification;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.tag.OrgTagTargetCmd;
import cloud.xcan.angus.core.gm.application.query.tag.OrgTagTargetQuery;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.DeptTagFacade;
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
public class DeptTagFacadeImpl implements DeptTagFacade {

  @Resource
  private OrgTagTargetCmd orgTagTargetCmd;

  @Resource
  private OrgTagTargetQuery orgTagTargetQuery;

  @Override
  public List<IdKey<Long, Object>> tagAdd(Long deptId, LinkedHashSet<Long> tagIds) {
    return orgTagTargetCmd.deptTagAdd(deptId, tagIds);
  }

  @Override
  public void tagReplace(Long id, LinkedHashSet<Long> ids) {
    orgTagTargetCmd.deptTagReplace(id, ids);
  }

  @Override
  public void tagDelete(Long deptId, HashSet<Long> tagIds) {
    orgTagTargetCmd.deptTagDelete(deptId, tagIds);
  }

  @NameJoin
  @Override
  public PageResult<OrgTagTargetVo> tagList(Long deptId, OrgTargetTagFindDto dto) {
    dto.setTargetId(deptId).setTargetType(OrgTargetType.DEPT);
    Page<OrgTagTarget> tagTargetsPage = orgTagTargetQuery.findTargetTag(
        getTargetTagSpecification(dto), dto.tranPage());
    return buildVoPageResult(tagTargetsPage, OrgTagTargetAssembler::toTagTargetVo);
  }

}
