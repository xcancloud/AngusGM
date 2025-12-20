package cloud.xcan.angus.core.gm.interfaces.tag.facade.internal;

import static cloud.xcan.angus.spec.BizConstant.buildVoPageResult;
import static cloud.xcan.angus.spec.BizConstant.getMatchSearchFields;

import cloud.xcan.angus.core.gm.application.cmd.tag.TagCmd;
import cloud.xcan.angus.core.gm.application.query.tag.TagQuery;
import cloud.xcan.angus.core.gm.domain.tag.Tag;
import cloud.xcan.angus.core.gm.domain.tag.TagRepo;
import cloud.xcan.angus.core.gm.domain.tag.enums.TagStatus;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.TagFacade;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagCreateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.TagAssembler;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagDetailVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagListVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Implementation of tag facade
 */
@Service
public class TagFacadeImpl implements TagFacade {

  @Resource
  private TagCmd tagCmd;

  @Resource
  private TagQuery tagQuery;

  @Resource
  private TagRepo tagRepo;

  @Override
  public TagDetailVo create(TagCreateDto dto) {
    Tag tag = TagAssembler.toCreateDomain(dto);
    Tag saved = tagCmd.create(tag);
    return TagAssembler.toDetailVo(saved);
  }

  @Override
  public TagDetailVo update(Long id, TagUpdateDto dto) {
    Tag tag = TagAssembler.toUpdateDomain(id, dto);
    Tag saved = tagCmd.update(tag);
    return TagAssembler.toDetailVo(saved);
  }

  @Override
  public void enable(Long id) {
    tagCmd.enable(id);
  }

  @Override
  public void disable(Long id) {
    tagCmd.disable(id);
  }

  @Override
  public void delete(Long id) {
    tagCmd.delete(id);
  }

  @Override
  public TagDetailVo getDetail(Long id) {
    Tag tag = tagQuery.findAndCheck(id);
    return TagAssembler.toDetailVo(tag);
  }

  @Override
  public PageResult<TagListVo> list(TagFindDto dto) {
    GenericSpecification<Tag> spec = TagAssembler.getSpecification(dto);
    Page<Tag> page = tagQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, TagAssembler::toListVo);
  }

  @Override
  public TagStatsVo getStats() {
    TagStatsVo stats = new TagStatsVo();
    
    long totalTags = tagRepo.count();
    long enabledTags = tagRepo.countByStatus(TagStatus.ENABLED);
    long disabledTags = tagRepo.countByStatus(TagStatus.DISABLED);
    
    // Count unique categories
    // TODO: Implement category count query
    long categoryCount = 0L;
    
    stats.setTotalTags(totalTags);
    stats.setEnabledTags(enabledTags);
    stats.setDisabledTags(disabledTags);
    stats.setCategoryCount(categoryCount);
    
    // TODO: Calculate total usage count
    stats.setTotalUsageCount(0L);
    
    return stats;
  }

  @Override
  public List<TagDetailVo> getByCategory(String category) {
    List<Tag> tags = tagQuery.findByCategory(category);
    return tags.stream()
        .map(TagAssembler::toDetailVo)
        .collect(Collectors.toList());
  }
}
