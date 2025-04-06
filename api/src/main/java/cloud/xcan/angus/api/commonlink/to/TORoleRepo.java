package cloud.xcan.angus.api.commonlink.to;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository("commonTORoleRepo")
public interface TORoleRepo extends BaseRepository<TORole, Long> {

  List<TORole> findByCodeIn(Collection<String> codes);

  Optional<TORole> findByCode(String code);

  List<TORole> findByNameIn(Collection<String> names);

  List<TORole> findAllByIdIn(Collection<Long> ids);

  List<TORole> findByIdIn(Set<Long> ids);

  boolean existsByCodeAndIdNot(String code, Long id);

  boolean existsByCode(String code);

  boolean existsByNameAndIdNot(String name, Long id);

  boolean existsByName(String name);

  @Modifying
  @Query(value = "DELETE FROM to_role WHERE id IN ?1", nativeQuery = true)
  void deleteByIdIn(Collection<Long> ids);
}
