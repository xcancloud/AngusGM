package cloud.xcan.angus.core.gm.domain.email;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Email template repository interface
 */
@NoRepositoryBean
public interface EmailTemplateRepo extends BaseRepository<EmailTemplate, Long> {

  /**
   * Check if template code exists
   */
  boolean existsByCode(String code);

  /**
   * Check if template code exists excluding specific id
   */
  boolean existsByCodeAndIdNot(String code, Long id);

  /**
   * Find template by code
   */
  EmailTemplate findByCode(String code);
}

