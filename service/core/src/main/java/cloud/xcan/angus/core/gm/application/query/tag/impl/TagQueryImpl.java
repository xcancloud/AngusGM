package cloud.xcan.angus.core.gm.application.query.tag.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.tag.TagQuery;
import cloud.xcan.angus.core.gm.domain.tag.Tag;
import cloud.xcan.angus.core.gm.domain.tag.TagRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Implementation of tag query service
 */
@Biz
public class TagQueryImpl implements TagQuery {

  @Resource
  private TagRepo tagRepo;

  @Override
  public Tag findAndCheck(Long id) {
    return new BizTemplate<Tag>() {
      @Override
      protected Tag process() {
        return tagRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("标签未找到", new Object[]{}));
      }
    }.execute();
  }

  @Override
  public Page<Tag> find(GenericSpecification<Tag> spec, PageRequest pageable,
                        boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Tag>>() {
      @Override
      protected Page<Tag> process() {
        Page<Tag> page = tagRepo.findAll(spec, pageable);
        
        // Set associated data if needed
        if (page.hasContent()) {
          // TODO: Set usage counts
        }
        
        return page;
      }
    }.execute();
  }

  @Override
  public List<Tag> findByCategory(String category) {
    return new BizTemplate<List<Tag>>() {
      @Override
      protected List<Tag> process() {
        return tagRepo.findByCategory(category);
      }
    }.execute();
  }

  @Override
  public boolean existsByName(String name) {
    return tagRepo.existsByName(name);
  }
}
