package cloud.xcan.angus.core.gm.interfaces.tag.facade;

import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagCreateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagAllVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagDetailVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagListVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

/**
 * Tag facade interface
 */
public interface TagFacade {

  /**
   * Create tag
   */
  TagDetailVo create(TagCreateDto dto);

  /**
   * Update tag
   */
  TagDetailVo update(Long id, TagUpdateDto dto);

  /**
   * Delete tag
   */
  void delete(Long id);

  /**
   * Get tag detail
   */
  TagDetailVo getDetail(Long id);

  /**
   * List tags with pagination
   */
  PageResult<TagListVo> list(TagFindDto dto);

  /**
   * Get all tags (without pagination)
   */
  List<TagAllVo> getAll();
}
