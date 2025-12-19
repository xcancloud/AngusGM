package cloud.xcan.angus.core.gm.infra.persistence.postgres.tag;

import cloud.xcan.angus.core.gm.domain.tag.Tag;
import cloud.xcan.angus.core.gm.domain.tag.TagRepo;
import org.springframework.stereotype.Repository;

/**
 * PostgreSQL implementation of TagRepo
 */
@Repository
public interface TagRepoPostgres extends TagRepo {
  // Spring Data JPA will implement methods automatically
}
