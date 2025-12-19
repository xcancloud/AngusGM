package cloud.xcan.angus.core.gm.application.query.user;

import cloud.xcan.angus.core.gm.domain.user.User;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * User query service interface
 */
public interface UserQuery {

  /**
   * Find user by id and check existence
   */
  User findAndCheck(Long id);

  /**
   * Find users with pagination
   */
  Page<User> find(GenericSpecification<User> spec, PageRequest pageable,
                  boolean fullTextSearch, String[] match);

  /**
   * Check if username exists
   */
  boolean existsByUsername(String username);

  /**
   * Check if email exists
   */
  boolean existsByEmail(String email);
}
