package cloud.xcan.angus.core.gm.application.query.group;

import cloud.xcan.angus.core.gm.domain.group.Group;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Group query service interface
 */
public interface GroupQuery {

  /**
   * Find group by id and check existence
   */
  Group findAndCheck(Long id);

  /**
   * Find groups with pagination
   */
  Page<Group> find(GenericSpecification<Group> spec, PageRequest pageable,
                   boolean fullTextSearch, String[] match);

  /**
   * Check if code exists
   */
  boolean existsByCode(String code);
}
