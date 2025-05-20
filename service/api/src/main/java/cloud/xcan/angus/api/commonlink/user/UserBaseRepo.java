package cloud.xcan.angus.api.commonlink.user;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("commonUserBaseRepo")
public interface UserBaseRepo extends NameJoinRepository<UserBase, Long>,
    BaseRepository<UserBase, Long> {

  @Override
  @Query(value = "SELECT * FROM user0 WHERE id IN ?1", nativeQuery = true)
  List<UserBase> findByIdIn(Collection<Long> ids);

  @Override
  Optional<UserBase> findById(Long id);

  @Query(value = "SELECT id FROM UserBase")
  Page<Long> findAllIds(Pageable pageable);

  @Query(value = "SELECT u FROM UserBase u "
      + "where u.id IN (?1) AND u.deleted = false AND u.enabled = true AND u.expired = false AND u.locked = false")
  List<UserBase> findValidByIdIn(Collection<Long> ids);

  @Query(value = "SELECT u FROM UserBase u "
      + "where u.id = ?1 AND u.deleted = false AND u.enabled = true AND u.expired = false AND u.locked = false")
  Optional<UserBase> findValidById(Long id);

  List<UserBase> findByFullNameIn(Collection<String> names);

  List<UserBase> findAllByTenantId(Long tenantId);

  UserBase findByUsername(String username);

}
