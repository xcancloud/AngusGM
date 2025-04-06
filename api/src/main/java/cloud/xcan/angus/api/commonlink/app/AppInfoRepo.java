package cloud.xcan.angus.api.commonlink.app;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository("commonAppInfoRepo")
public interface AppInfoRepo extends BaseRepository<AppInfo, Long>,
    NameJoinRepository<AppInfo, Long> {

  @Override
  Page<AppInfo> findAll(Specification<AppInfo> spc, Pageable pageable);

  List<AppInfo> findAllByIdIn(Collection<Long> ids);

  List<AppInfo> findAllByCode(String code);

  Optional<AppInfo> findByEditionTypeAndCodeAndVersion(EditionType editionType, String code,
      String version);

  @Query(value = "SELECT * FROM app WHERE code = ?1 AND enabled = true ORDER BY id DESC limit 1", nativeQuery = true)
  AppInfo findLatestByCode(String code);

  @Query(value = "SELECT a FROM AppInfo a WHERE a.id IN ?1 AND a.name like %?2%")
  List<AppInfo> findAppNameLike(Collection<Long> ids, String name);

  @Query(value = "SELECT a FROM AppInfo a WHERE  a.name like %?1%")
  List<AppInfo> findAppNameLike(String name);

  boolean existsByCodeAndVersionAndIdNot(String code, String version, Long id);

  boolean existsByCodeAndVersion(String code, String version);

}
