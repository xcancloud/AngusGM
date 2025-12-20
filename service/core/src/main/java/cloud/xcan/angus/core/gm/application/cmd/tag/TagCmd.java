package cloud.xcan.angus.core.gm.application.cmd.tag;

import cloud.xcan.angus.core.gm.domain.tag.Tag;

/**
 * Tag command service interface
 */
public interface TagCmd {

  /**
   * Create tag
   */
  Tag create(Tag tag);

  /**
   * Update tag
   */
  Tag update(Tag tag);

  /**
   * Delete tag
   */
  void delete(Long id);

  /**
   * Enable tag
   */
  void enable(Long id);

  /**
   * Disable tag
   */
  void disable(Long id);
}
