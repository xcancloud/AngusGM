package cloud.xcan.angus.core.gm.domain.tag;

import cloud.xcan.angus.core.gm.domain.tag.enums.TagStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Tag repository interface
 */
@NoRepositoryBean
public interface TagRepo extends BaseRepository<Tag, Long> {

  /**
   * Check if name exists
   */
  boolean existsByName(String name);

  /**
   * Check if name exists excluding specific id
   */
  boolean existsByNameAndIdNot(String name, Long id);

  /**
   * Find tags by category
   */
  List<Tag> findByCategory(String category);

  /**
   * Find tags by status
   */
  List<Tag> findByStatus(TagStatus status);

  /**
   * Count tags by status
   */
  long countByStatus(TagStatus status);

  /**
   * Count tags by category
   */
  long countByCategory(String category);
}
