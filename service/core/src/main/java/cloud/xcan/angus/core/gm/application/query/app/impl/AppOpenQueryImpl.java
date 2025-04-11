package cloud.xcan.angus.core.gm.application.query.app.impl;

import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.APP_OPEN_EXPIRED_T;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.APP_OPEN_NOT_FOUND_T;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getApplicationInfo;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isCloudServiceEdition;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isOpClient;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isPrivateEdition;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isTenantClient;
import static cloud.xcan.angus.spec.experimental.BizConstant.XCAN_TENANT_PLATFORM_CODE;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.app.open.AppOpen;
import cloud.xcan.angus.api.commonlink.app.open.AppOpenRepo;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.app.AppOpenQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class AppOpenQueryImpl implements AppOpenQuery {

  @Resource
  private AppOpenRepo appOpenRepo;

  @Resource
  private AppQuery appQuery;

  @Resource
  private WebTagQuery webTagQuery;

  @Override
  public Page<AppOpen> find(GenericSpecification<AppOpen> spec, PageRequest pageable) {
    return new BizTemplate<Page<AppOpen>>(false) {

      @Override
      protected Page<AppOpen> process() {
        if (!isOpClient()) {
          spec.getCriteria().add(SearchCriteria.equal("tenantId", getTenantId()));
        }

        // spec.getCriteria().add(SearchCriteria.equal("clientId", getClientId()));

        if (isPrivateEdition() || (isTenantClient() && isCloudServiceEdition())) {
          spec.getCriteria()
              .add(SearchCriteria.equal("editionType", getApplicationInfo().getEditionType()));
        }

        Page<AppOpen> page = appOpenRepo.findAll(spec, pageable);
        if (page.hasContent()) {
          List<App> apps = appQuery.checkAndFind(page.getContent().stream()
              .map(AppOpen::getAppId).collect(Collectors.toSet()), false);
          webTagQuery.setAppTags(apps);
          PrincipalContext.addExtension("apps", apps);
        }
        return page;
      }
    }.execute();
  }

  @Override
  public AppOpen checkAndFind(Long appId, Long tenantId, boolean checkExpired) {
    return checkAndFind(Collections.singletonList(appId), tenantId, checkExpired).get(0);
  }

  @Override
  public List<AppOpen> checkAndFind(Collection<Long> appIds, Long tenantId, boolean checkExpired) {
    List<AppOpen> appOpens = appOpenRepo.findByAppIdInAndTenantId(appIds, tenantId);
    if (isEmpty(appOpens)) {
      throw ResourceNotFound.of(APP_OPEN_NOT_FOUND_T, new Object[]{tenantId,
          appIds.iterator().next()});
    }
    if (checkExpired) {
      Map<Long, List<AppOpen>> appOpensMap = appOpens.stream().collect(
          Collectors.groupingBy(AppOpen::getAppId));
      for (Long appId : appOpensMap.keySet()) {
        if (appOpensMap.get(appId).stream().allMatch(AppOpen::getExpirationDeleted)) {
          throw ResourceNotFound.of(APP_OPEN_EXPIRED_T, new Object[]{tenantId, appId});
        }
      }
    }
    return appOpens;
  }

  @Override
  public List<AppOpen> findOpenedAppByTenantId(Long realOptTenantId) {
    return appOpenRepo.findValidByTenantId(realOptTenantId,
        // Fix::The clientId is empty when called by the innerapi
        isEmpty(PrincipalContext.getClientId()) ? XCAN_TENANT_PLATFORM_CODE
            : PrincipalContext.getClientId());
  }

  @Override
  public List<Long> findValidAppIdsByTenantId(Long realOptTenantId) {
    return appOpenRepo.findValidAppIdsByTenantId(realOptTenantId, PrincipalContext.getClientId());
  }

  @Override
  public List<Long> findValidAppIdsByTenantIdAndEditionType(Long realOptTenantId,
      EditionType editionType) {
    return appOpenRepo.findValidAppIdsByTenantIdAdnEditionType(realOptTenantId,
        editionType.getValue(), PrincipalContext.getClientId());
  }
}
