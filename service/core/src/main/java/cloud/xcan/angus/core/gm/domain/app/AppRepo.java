package cloud.xcan.angus.core.gm.domain.app;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface AppRepo extends BaseRepository<App, Long>, NameJoinRepository<App, Long> {

  @Override
  Page<App> findAll(Specification<App> spc, Pageable pageable);

  List<App> findAllByIdIn(Collection<Long> ids);

  List<App> findAllByCode(String code);

  Optional<App> findByEditionTypeAndCodeAndVersion(EditionType editionType, String appCode,
      String version);

  @Query(value = "SELECT * FROM app WHERE code = ?1 AND enabled = true ORDER BY id DESC limit 1", nativeQuery = true)
  App findLatestByCode(String code);

  @Query(value = "SELECT * FROM app WHERE code = ?1 AND edition_type = ?2 AND enabled = true ORDER BY id DESC limit 1", nativeQuery = true)
  App findLatestByCodeAndEditionType(String code, String editionType);

  @Query(value = "SELECT a FROM App a WHERE a.id IN ?1 AND a.name like %?2%")
  List<App> findAppNameLike(Collection<Long> ids, String name);

  @Query(value = "SELECT a FROM App a WHERE  a.name like %?1%")
  List<App> findAppNameLike(String name);

  boolean existsByCodeAndEditionTypeAndVersionAndIdNot(String code, EditionType editionType,
      String version, Long id);

  boolean existsByCodeAndEditionTypeAndVersion(String code, EditionType editionType,
      String version);

  @Modifying
  @Query(value = "DELETE FROM app WHERE id IN ?1", nativeQuery = true)
  void deleteByIdIn(Collection<Long> ids);

}
