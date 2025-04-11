package cloud.xcan.angus.core.gm.application.query.app;

import cloud.xcan.angus.api.commonlink.app.open.AppOpen;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface AppOpenQuery {

  Page<AppOpen> find(GenericSpecification<AppOpen> spec, PageRequest pageable);

  AppOpen checkAndFind(Long appId, Long tenantId, boolean checkExpired);

  List<AppOpen> checkAndFind(Collection<Long> appIds, Long tenantId, boolean checkExpired);

  List<AppOpen> findOpenedAppByTenantId(Long realOptTenantId);

  List<Long> findValidAppIdsByTenantId(Long realOptTenantId);

  List<Long> findValidAppIdsByTenantIdAndEditionType(Long realOptTenantId, EditionType editionType);
}
