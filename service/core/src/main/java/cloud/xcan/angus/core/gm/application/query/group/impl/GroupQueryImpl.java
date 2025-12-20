package cloud.xcan.angus.core.gm.application.query.group.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.domain.group.Group;
import cloud.xcan.angus.core.gm.domain.group.GroupRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Implementation of group query service
 */
@Biz
public class GroupQueryImpl implements GroupQuery {

  @Resource
  private GroupRepo groupRepo;

  @Override
  public Group findAndCheck(Long id) {
    return new BizTemplate<Group>() {
      @Override
      protected Group process() {
        return groupRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("组未找到", new Object[]{}));
      }
    }.execute();
  }

  @Override
  public Page<Group> find(GenericSpecification<Group> spec, PageRequest pageable,
                          boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Group>>() {
      @Override
      protected Page<Group> process() {
        Page<Group> page = groupRepo.findAll(spec, pageable);
        
        // Set associated data if needed
        if (page.hasContent()) {
          // TODO: Set owner names, member counts
        }
        
        return page;
      }
    }.execute();
  }

  @Override
  public boolean existsByCode(String code) {
    return groupRepo.existsByCode(code);
  }
}
