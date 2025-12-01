package cloud.xcan.angus.core.gm.domain.auth;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface AuthUserTokenRepo extends BaseRepository<AuthUserToken, Long> {

  boolean existsByName(String name);

  long countByTenantIdAndCreatedBy(Long tenantId, Long userId);

  List<AuthUserToken> findByIdInAndCreatedBy(Collection<Long> ids, Long userId);

  List<AuthUserToken> findAllByCreatedBy(Long userId);

  List<AuthUserToken> findAllByCreatedByAndGenerateAppCode(Long userId, String appCode);

  @Modifying
  @Query(value = "DELETE FROM auth_user_token WHERE id IN ?1", nativeQuery = true)
  void deleteByIdIn(Collection<Long> ids);

}
