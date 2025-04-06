package cloud.xcan.angus.core.gm.interfaces.tag.facade;

import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagAddDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagSearchDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.WebTagDetailVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;


public interface WebTagFacade {

  List<IdKey<Long, Object>> add(List<WebTagAddDto> dto);

  void update(List<WebTagUpdateDto> dto);

  void delete(HashSet<Long> ids);

  WebTagDetailVo detail(Long id);

  PageResult<WebTagDetailVo> list(WebTagFindDto dto);

  PageResult<WebTagDetailVo> search(WebTagSearchDto dto);
}
