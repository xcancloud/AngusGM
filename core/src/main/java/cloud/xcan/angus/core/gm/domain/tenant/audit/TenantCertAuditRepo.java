package cloud.xcan.angus.core.gm.domain.tenant.audit;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TenantCertAuditRepo extends BaseRepository<TenantCertAudit, Long> {

  Optional<TenantCertAudit> findByTenantId(Long tenantId);

}
