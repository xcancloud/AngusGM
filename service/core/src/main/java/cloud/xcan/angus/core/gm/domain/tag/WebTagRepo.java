package cloud.xcan.angus.core.gm.domain.tag;

import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface WebTagRepo extends BaseRepository<WebTag, Long> {

  List<WebTag> findByNameIn(Collection<String> appTagNames);

  List<WebTag> findByIdIn(Collection<Long> ids);

  @Modifying
  @Query(value = "DELETE FROM web_tag WHERE id IN ?1", nativeQuery = true)
  void deleteByIdIn(Collection<Long> tagIds);
}
