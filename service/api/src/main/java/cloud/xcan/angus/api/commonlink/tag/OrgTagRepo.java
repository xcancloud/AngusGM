package cloud.xcan.angus.api.commonlink.tag;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("commonOrgTagRepo")
public interface OrgTagRepo extends BaseRepository<OrgTag, Long> {

  List<OrgTag> findByNameIn(Collection<String> names);

  Integer countByTenantId(Long tenantId);

  List<OrgTag> findByIdIn(Collection<Long> tagIds);

  List<OrgTag> findAllByTenantIdAndNameIn(Long tenantId, Set<String> names);

  @Modifying
  @Query("DELETE FROM OrgTag WHERE id IN (?1)")
  void deleteByIdIn(HashSet<Long> ids);
}
