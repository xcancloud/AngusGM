package cloud.xcan.angus.core.gm.application.query.tag;

import cloud.xcan.angus.core.gm.domain.tag.Tag;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Tag query service interface
 */
public interface TagQuery {

  /**
   * Find tag by id and check existence
   */
  Tag findAndCheck(Long id);

  /**
   * Find tags with pagination
   */
  Page<Tag> find(GenericSpecification<Tag> spec, PageRequest pageable,
                 boolean fullTextSearch, String[] match);

  /**
   * Find tags by category
   */
  List<Tag> findByCategory(String category);

  /**
   * Find all tags
   */
  List<Tag> findAll();

  /**
   * Check if name exists
   */
  boolean existsByName(String name);
}
