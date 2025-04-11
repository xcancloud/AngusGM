package cloud.xcan.angus.core.gm.domain.user.directory;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository("commonUserDirectoryRepo")
public interface UserDirectoryRepo extends BaseRepository<UserDirectory, Long> {

  long countByName(String name);

  long countByNameAndIdNot(String name, Long id);

  @Query(value = "SELECT sequence FROM user_directory ORDER BY sequence DESC LIMIT 1", nativeQuery = true)
  Integer findMaxSequence();
}
