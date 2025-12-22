package cloud.xcan.angus.core.gm.application.query.interfaces;

import cloud.xcan.angus.core.gm.domain.interfaces.Interface;
import cloud.xcan.angus.core.gm.domain.interfaces.enums.InterfaceStatus;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Interface query service interface
 */
public interface InterfaceQuery {

  /**
   * Find interface by id and check existence
   */
  Interface findAndCheck(Long id);

  /**
   * Find interfaces with pagination
   */
  Page<Interface> find(GenericSpecification<Interface> spec, PageRequest pageable,
                       boolean fullTextSearch, String[] match);

  /**
   * Find interfaces by service id
   */
  Page<Interface> findByServiceId(Long serviceId, GenericSpecification<Interface> spec, 
                                   PageRequest pageable, boolean fullTextSearch, String[] match);

  /**
   * Find interfaces by tag
   */
  Page<Interface> findByTag(String tag, GenericSpecification<Interface> spec,
                            PageRequest pageable, boolean fullTextSearch, String[] match);

  /**
   * Get all services with interface count
   */
  List<Interface> findAllServices();

  /**
   * Get all tags with interface count
   */
  List<String> findAllTags();

  /**
   * Count total interfaces
   */
  long countTotal();

  /**
   * Count interfaces by status
   */
  long countByStatus(InterfaceStatus status);

  /**
   * Count interfaces by service id
   */
  long countByServiceId(Long serviceId);
}
