package cloud.xcan.angus.core.gm.application.query.app;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AppQuery {

  App detail(Long id);

  App detail(String code, EditionType editionType);

  Page<App> find(GenericSpecification<App> spec, Pageable pageable);

  App findLatestByCode(String code);

  App findLatestByCode(String code, EditionType editionType);

  List<App> findById(Collection<Long> ids);

  Map<Long, App> findMapById(Collection<Long> ids);

  App checkAndFind(String idOrCode);

  App checkAndFind(String code, EditionType editionType);

  App checkAndFind(Long appId, boolean checkEnabled);

  App checkAndFind(EditionType editionType, String appCode, String version, boolean checkEnabled);

  List<App> checkAndFind(Collection<Long> appIds, boolean checkEnabled);

  List<App> checkAndFindTenantApp(Collection<Long> appIds, boolean checkEnabled);

  void checkUniqueCodeAndVersion(App app);

}
