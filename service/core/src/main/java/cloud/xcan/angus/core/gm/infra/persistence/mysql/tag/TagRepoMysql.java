package cloud.xcan.angus.core.gm.infra.persistence.mysql.tag;

import cloud.xcan.angus.core.gm.domain.tag.Tag;
import cloud.xcan.angus.core.gm.domain.tag.TagRepo;
import org.springframework.stereotype.Repository;

/**
 * MySQL implementation of TagRepo
 */
@Repository
public interface TagRepoMysql extends TagRepo {
  // Spring Data JPA will implement methods automatically
}
