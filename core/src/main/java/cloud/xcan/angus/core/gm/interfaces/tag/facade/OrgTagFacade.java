package cloud.xcan.angus.core.gm.interfaces.tag.facade;

import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagAddDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagSearchDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.OrgTagDetailVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;


public interface OrgTagFacade {

  List<IdKey<Long, Object>> add(List<OrgTagAddDto> dto);

  void update(List<OrgTagUpdateDto> dto);

  void delete(HashSet<Long> ids);

  OrgTagDetailVo detail(Long id);

  PageResult<OrgTagDetailVo> list(OrgTagFindDto dto);

  PageResult<OrgTagDetailVo> search(OrgTagSearchDto dto);
}
