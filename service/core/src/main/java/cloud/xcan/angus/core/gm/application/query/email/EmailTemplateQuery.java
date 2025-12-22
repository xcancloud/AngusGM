package cloud.xcan.angus.core.gm.application.query.email;

import cloud.xcan.angus.core.gm.domain.email.EmailTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Email template query service interface
 */
public interface EmailTemplateQuery {

  /**
   * Find template by id and check existence
   */
  EmailTemplate findAndCheck(Long id);

  /**
   * Find templates with pagination
   */
  Page<EmailTemplate> find(GenericSpecification<EmailTemplate> spec, PageRequest pageable);

  /**
   * Check if template code exists
   */
  boolean existsByCode(String code);

  /**
   * Check if template code exists excluding specific id
   */
  boolean existsByCodeAndIdNot(String code, Long id);
}

